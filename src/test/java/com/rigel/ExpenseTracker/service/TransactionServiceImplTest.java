package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.Role;
import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.engine.support.hierarchical.ThrowableCollector;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock private UserRepository mockUserRepo;
    @Mock private ExpenseCategoryRepository mockExpenseCategoryRepo;
    @Mock private IncomeCategoryRepository mockIncomeCategoryRepo;
    @Mock private ExpenseTransactionRepository mockExpenseTransactionRepo;
    @Mock private IncomeTransactionRepository mockIncomeTransactionRepo;
    @Mock private TransactionService mockTransactionService;

    @BeforeAll
    public static void beforeAll() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getDetails()).thenReturn(
                new User("koko", "koko", "Koko", "Borimechkov", "koko@gmail.com", 9000.0));
    }

    @BeforeEach
    void setUp() {
//        autoCloseable = MockitoAnnotations.openMocks(this);
        mockTransactionService = new TransactionServiceImpl(mockUserRepo, mockExpenseCategoryRepo,
                mockIncomeCategoryRepo, mockExpenseTransactionRepo,mockIncomeTransactionRepo);
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("koko");
    }
//
//    @AfterEach
//    void afterEach() {
//        mockUserRepo.deleteAll();
//        mockExpenseTransactionRepo.deleteAll();
//        mockExpenseCategoryRepo.deleteAll();
//        mockIncomeTransactionRepo.deleteAll();
//        mockIncomeTransactionRepo.deleteAll();
//    }

    @Test
    void saveCategoryToDBTest() {
        User user = getOneNormalUser();
        ExpenseCategory expenseCategory =getOneCategory(user, "food");
        when(mockUserRepo.findUserByUsername("koko")).thenReturn(Optional.of(user));

        mockTransactionService.saveCategoryToDB("FOOD", "expense");

        ArgumentCaptor<ExpenseCategory> categoryArgumentCaptor
                = ArgumentCaptor.forClass(ExpenseCategory.class);
        verify(mockExpenseCategoryRepo).saveAndFlush(categoryArgumentCaptor.capture());
        ExpenseCategory capturedCategory = categoryArgumentCaptor.getValue();

        assertThat(capturedCategory).isEqualTo(expenseCategory);
    }

    @Test
    void saveTransactionToDBTest() {
        User user = getOneNormalUser();
        ExpenseTransaction transaction = getOneTransaction(user);
        when(mockUserRepo.findUserByUsername("koko")).thenReturn(Optional.of(user));

        mockTransactionService.saveTransactionToDB(LocalDate.parse("2021-12-31"), 90.0, "food",
                "Bought some groceries", "expense");

        ArgumentCaptor<ExpenseTransaction> transactionArgumentCaptor
                = ArgumentCaptor.forClass(ExpenseTransaction.class);
        verify(mockExpenseTransactionRepo).saveAndFlush(transactionArgumentCaptor.capture());
        ExpenseTransaction capturedTransaction = transactionArgumentCaptor.getValue();

        assertThat(capturedTransaction).isEqualTo(transaction);
    }

    @Test
    void numberOfTransactionsByCategoryTest() {
        User user = getSecondNormalUser();
        List<ExpenseTransaction> transactions = setOfTransactions(getOneNormalUser(), getSecondNormalUser()).stream()
                .filter(t -> t.getUser().getUsername().equals("dani"))
                .filter(t -> t.getCategoryName().equals("food")).collect(Collectors.toList());
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("dani");
        lenient().when(mockUserRepo.findUserByUsername("dani")).thenReturn(Optional.of(user));
        when(mockExpenseTransactionRepo.findExpenseTransactionsByCategoryName("food")).thenReturn(transactions);

        int wanted = mockTransactionService
                .numberOfTransactionsByCategory("expense", "food");

        assertThat(wanted).isEqualTo(transactions.size());
    }

    @Test
    void getCategoryTest() {
        User user = getOneNormalUser();
        ExpenseCategory expenseCategory = getOneCategory(user, "food");
        when(mockExpenseCategoryRepo.findExpenseCategoryByCategoryNameAndUser
                (expenseCategory.getCategoryName(), expenseCategory.getUser()))
                .thenReturn(Optional.of(expenseCategory));
        when(mockUserRepo.findUserByUsername("koko")).thenReturn(Optional.of(user));

        Optional<?> result = mockTransactionService.getCategory("expense", "food");

        assertThat(result.get()).isEqualTo(expenseCategory);

    }

    @Test
    void notGettingCategoryTest() {
        User user = getOneNormalUser();
        when(mockUserRepo.findUserByUsername("koko")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> mockTransactionService.getCategory("expense", "travel"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"Category with this name doesn't exist.\"");

    }

    @Test
    void getCategoriesTest() {
        User user = getOneNormalUser();
        Set<ExpenseCategory> categories = setOfCategories(user);
        when(mockUserRepo.findUserByUsername("koko")).thenReturn(Optional.of(user));
        when(mockExpenseCategoryRepo.findExpenseCategoriesByUser(user)).thenReturn(categories);

        HashMap<String, Object> result = mockTransactionService.getCategories("expense");

        assertThat(result.get("totalCategories")).isEqualTo(categories);
    }

    @Test
    void getTransactionByIdTest() {
        List<ExpenseTransaction> transactions = setOfTransactions(getOneNormalUser(), getSecondNormalUser());
        Long id = 2L;
        when(mockUserRepo.findUserByUsername("koko")).thenReturn(Optional.of(transactions.get(0).getUser()));
        Optional<ExpenseTransaction> transaction = transactions
                .stream().filter(t -> t.getExpenseTransactionId().equals(id))
                .findFirst();

        Optional<?> result = mockTransactionService.getTransactionById("expense", 2L);

        assertThat(result).isEqualTo(transaction);
    }

    @Test
    void getTransactionsByCategoryAndUsernameTest() {
        List<ExpenseTransaction> transactions = setOfTransactions(getOneNormalUser(), getSecondNormalUser()).stream()
                .filter(t -> t.getUser().getUsername().equals("dani"))
                .filter(t -> t.getCategoryName().equals("food")).collect(Collectors.toList());
        Pageable pageable = PageRequest.of(1, transactions.size());
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("dani");
        when(mockExpenseTransactionRepo.filterTransactionsByUsernameAndCategory(pageable, "dani", "food"))
                .thenReturn(new PageImpl<>(transactions));

        HashMap<String, Object> result = mockTransactionService.getTransactionsByCategoryAndUsername(pageable, "expense", "food");

        assertThat(result.get("transactions")).isEqualTo(transactions);
    }

    @Test
    void getTransactionByDate() {
        List<ExpenseTransaction> transactions = setOfTransactions(getOneNormalUser(), getSecondNormalUser()).stream()
                .filter(t -> t.getUser().getUsername().equals("koko"))
                .filter(t -> t.getDate().toString().equals("2021-12-31")).collect(Collectors.toList());
        Pageable pageable = PageRequest.of(1, transactions.size());

        when(mockExpenseTransactionRepo.filterTransactionsByDate(pageable, "koko", LocalDate.parse("2021-12-31")))
                .thenReturn(new PageImpl<>(transactions));

        HashMap<String, Object> result = mockTransactionService.getTransactionByDate(pageable, "2021-12-31", "expense");

        assertThat(result.get("transactions")).isEqualTo(transactions);
    }

    @Test
    void getAllUserTransactions() {
        List<ExpenseTransaction> transactions = setOfTransactions(getOneNormalUser(), getSecondNormalUser()).stream()
                .filter(t -> t.getUser().getUsername().equals("dani"))
                .collect(Collectors.toList());
        Pageable pageable = PageRequest.of(1, transactions.size());
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("dani");
        when(mockExpenseTransactionRepo.filterTransactionsByDate(pageable, "dani", LocalDate.parse("2021-12-31")))
                .thenReturn(new PageImpl<>(transactions));

        HashMap<String, Object> result = mockTransactionService.getTransactionByDate(pageable, "2021-12-31", "expense");

        assertThat(result.get("transactions")).isEqualTo(transactions);
    }

    @Test
    void addCategory() {
    }

    @Test
    void addTransaction() {
    }

    @Test
    void transactionExists() {
    }

    @Test
    void categoryExists() {
    }

    @Test
    void deleteAllUserTransactions() {
    }

    @Test
    void deleteTransactionById() {
    }

    @Test
    void deleteTransactionsByCategoryTest() {
        User user = getOneNormalUser();
        ExpenseCategory category = getOneCategory(user, "food");
        List<ExpenseTransaction> transactions = setOfTransactions(user, getSecondNormalUser());
        category.setExpenseTransactions(transactions.stream()
                .filter(t -> t.getUser().getUsername().equals(user.getUsername()))
                .filter(t -> t.getCategoryName().equals("food"))
                .collect(Collectors.toList()));
        when(mockUserRepo.findUserByUsername("koko")).thenReturn(Optional.of(user));
        when(mockExpenseCategoryRepo.findExpenseCategoryByCategoryNameAndUser("food", user))
                .thenReturn(Optional.of(category));

        mockTransactionService.deleteTransactionsByCategory("expense","food");

        // TODO
    }

    @Test
    void deleteCategoryTest() {
        User user = getOneNormalUser();
        Set<ExpenseCategory> categories = setOfCategories(user)
                .stream().filter(c -> !c.getCategoryName().equals("food")).collect(Collectors.toSet());
        when(mockUserRepo.findUserByUsername("koko")).thenReturn(Optional.of(user));

        mockTransactionService.deleteCategory("food", "expense");

        assertThat(categories.stream().anyMatch(c -> c.getCategoryName().equals("food")))
                .isEqualTo(false);
    }

    private List<User> setOfUsers(){
        User user2 = new User(2L, "deni", "deni", "Deni", "Duhova", "deniduhova@gmail.com", 9794.0);
        User user3 = new User(3L,"koko", "koko", "Koko", "Bor", "kbor@gmail.com", 4643.0);
        User user4 = new User(4L, "desi", "desi", "Desi", "Popova", "desippv@gmail.com", 8151125.0);
        user2.setRoles(Set.of(new Role(Role.ROLE_USER)));
        user3.setRoles(Set.of(new Role(Role.ROLE_USER)));
        user4.setRoles(Set.of(new Role(Role.ROLE_USER)));
        List<User> users = List.of(user2,user3, user4);
        mockUserRepo.saveAll(users);
        return users;
    }

    private User getOneNormalUser(){
        User user = new User(999L, "koko", "koko", "Koko", "Bor", "kbor@gmail.com", 4643.0);
        user.setRoles(Set.of(new Role(Role.ROLE_USER)));
        mockUserRepo.save(user);
        return user;
    }
    private User getSecondNormalUser(){
        User secondUser = new User(555L,  "dani", "dani", "Dani", "Petkov", "danipptk@gmail.com", 8416.0);
        secondUser.setRoles(Set.of(new Role(Role.ROLE_USER)));
        mockUserRepo.save(secondUser);
        return secondUser;
    }
    private User getOneAdminUser(){
        User user = new User(1L, "admin", "admin", "Admin", "Admin", "admin@gmail.com", 10000.0);
        user.setRoles(Set.of(new Role(Role.ROLE_ADMIN)));
        mockUserRepo.save(user);
        return user;
    }

    private ExpenseCategory getOneCategory(User user, String categoryName){
        ExpenseCategory expenseCategory = new ExpenseCategory(categoryName, user);
        expenseCategory.setExpenseCategoryId(1L);
        user.setExpenseCategories(Set.of(expenseCategory));
        mockExpenseCategoryRepo.save(expenseCategory);
        return expenseCategory;
    }
    private Set<ExpenseCategory> setOfCategories(User user){
        ExpenseCategory expenseCategory = new ExpenseCategory(1L, "food", user);
        ExpenseCategory expenseCategory1 = new ExpenseCategory(2L,"travel", user);
        ExpenseCategory expenseCategory2 = new ExpenseCategory(3L, "business", user);
        ExpenseCategory expenseCategory3 = new ExpenseCategory(4L, "healthcare", user);

        Set<ExpenseCategory> result = Set.of(expenseCategory, expenseCategory1,expenseCategory2,expenseCategory3);
        mockExpenseCategoryRepo.saveAll(result);
        user.setExpenseCategories(result);
        return result;
    }

    private ExpenseTransaction getOneTransaction(User user){
        ExpenseTransaction transaction = new ExpenseTransaction(LocalDate.parse("2021-12-31"), 90.0, "food",
            "Bought some groceries", user);
        mockExpenseTransactionRepo.save(transaction);
        return transaction;
    }
    private List<ExpenseTransaction> setOfTransactions(User user, User secondUser){
        ExpenseCategory food = new ExpenseCategory(1L, "food", user);
        ExpenseCategory housing = new ExpenseCategory(2L,"housing", user);
        ExpenseCategory gifts = new ExpenseCategory(3L, "gifts", user);
        ExpenseCategory subscription = new ExpenseCategory(4L, "subscription", user);

        ExpenseTransaction first = new ExpenseTransaction(1L, LocalDate.parse("2021-12-31"), 90.0, "food",
                "Bought some groceries", user, food);
        ExpenseTransaction second = new ExpenseTransaction(2L, LocalDate.parse("2021-12-31"), 800.0, "housing",
                "Monthly rent payment.", user, food);
        ExpenseTransaction third = new ExpenseTransaction(3L, LocalDate.parse("2022-09-02"), 50.0, "subscriptions",
                "Yearly Netflix.", secondUser, subscription);
        ExpenseTransaction fourth = new ExpenseTransaction(4L, LocalDate.parse("2022-01-01"), 300.50, "gifts",
                "Gifts for friends", user, gifts);
        ExpenseTransaction fifth = new ExpenseTransaction(5L, LocalDate.parse("2021-12-01"), 60.0, "food",
                "Bought some groceries", user, food);
        ExpenseTransaction sixth = new ExpenseTransaction(6L, LocalDate.parse("2021-11-01"), 300.0, "food",
                "Monthly groceries.", secondUser, food);
        ExpenseTransaction seventh = new ExpenseTransaction(7L, LocalDate.parse("2021-09-25"), 600.0, "housing",
                "Monthly rent payment.", secondUser, housing);
        ExpenseTransaction eight = new ExpenseTransaction(8L, LocalDate.parse("2021-06-25"), 20.0, "subscription",
                "Amazon Delivery", secondUser, subscription);

        List<ExpenseTransaction> transactions = new ArrayList<>(List.of(first, second, fourth,fifth));
        List<ExpenseTransaction> secondUserTransactions = List.of(third, sixth, seventh,eight);

        user.setExpenseTransactions(transactions);
        secondUser.setExpenseTransactions(secondUserTransactions);
        mockExpenseTransactionRepo.saveAll(transactions);
        mockExpenseTransactionRepo.saveAll(secondUserTransactions);

        List<ExpenseTransaction> result = new ArrayList<>();
        result.addAll(transactions);
        result.addAll(secondUserTransactions);
        return result;
    }
}