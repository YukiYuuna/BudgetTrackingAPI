package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.Role;
import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.engine.support.hierarchical.ThrowableCollector;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
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
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("koko");
    }

    @BeforeEach
    void setUp() {
//        autoCloseable = MockitoAnnotations.openMocks(this);
        mockTransactionService = new TransactionServiceImpl(mockUserRepo, mockExpenseCategoryRepo,
                mockIncomeCategoryRepo, mockExpenseTransactionRepo,mockIncomeTransactionRepo);

    }

    @Test
    void saveCategoryToDBTest() {
        User user = new User("koko", "koko", "Koko", "Bor", "kbor@gmail.com", 4643.0);
        user.setRoles(Set.of(new Role(Role.ROLE_USER)));
        user.setUserId(1L);
        ExpenseCategory expenseCategory =getOneCategory(user);
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
        User user = new User("koko", "koko", "Koko", "Bor", "kbor@gmail.com", 4643.0);
        user.setRoles(Set.of(new Role(Role.ROLE_USER)));
        user.setUserId(1L);
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
    void numberOfTransactionsByCategory() {
        User user = new User("koko", "koko", "Koko", "Bor", "kbor@gmail.com", 4643.0);
        user.setRoles(Set.of(new Role(Role.ROLE_USER)));
        user.setUserId(1L);
        List<ExpenseTransaction> transactions = setOfTransactions(user).stream().filter(t -> t.getCategoryName().equals("food")).collect(Collectors.toList());
        when(mockExpenseTransactionRepo.findExpenseTransactionByCategoryName("food")).thenReturn(transactions);
        when(mockUserRepo.findUserByUsername("koko")).thenReturn(Optional.of(user));

        int wanted = mockTransactionService.numberOfTransactionsByCategory("expense", "food");

        assertThat(wanted).isEqualTo(transactions.size());
    }

    @Test
    void getCategory() {
        User user = getOneUser();
        ExpenseCategory expenseCategory = getOneCategory(user);
        when(mockExpenseCategoryRepo.findExpenseCategoryByCategoryNameAndUser
                (expenseCategory.getCategoryName(), expenseCategory.getUser()))
                .thenReturn(Optional.of(expenseCategory));
        when(mockUserRepo.findUserByUsername("koko")).thenReturn(Optional.of(user));

        Optional<?> result = mockTransactionService.getCategory("expense", "food");

        assertThat(result.get()).isEqualTo(expenseCategory);

    }

    @Test
    void notGettingCategory() {
        User user = getOneUser();
        when(mockUserRepo.findUserByUsername("koko")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> mockTransactionService.getCategory("expense", "travel"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"Category with this name doesn't exist.\"");

    }

    @Test
    void getCategories() {
        User user = getOneUser();
        Set<ExpenseCategory> categories = setOfCategories(user);
        when(mockUserRepo.findUserByUsername("koko")).thenReturn(Optional.of(user));
        when(mockExpenseCategoryRepo.findExpenseCategoriesByUser(user)).thenReturn(categories);

        HashMap<String, Object> result = mockTransactionService.getCategories("expense");

        assertThat(result.get("totalCategories")).isEqualTo(categories);
    }

    @Test
    void getTransactionById() {
        User user = getOneUser();
        when(mockUserRepo.findUserByUsername("koko")).thenReturn(Optional.of(user));
        List<ExpenseTransaction> transactions = setOfTransactions(user);

        Optional<?> result = mockTransactionService.getTransactionById("expense", 2L);

        assertThat(result.get()).isEqualTo(transactions.get(1));
    }

    @Test
    void getTransactionsByCategoryAndUsername() {
    }

    @Test
    void getTransactionByDate() {
    }

    @Test
    void getAllUserTransactions() {
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
    void deleteTransactionsByCategory() {
    }

    @Test
    void deleteCategory() {
    }

    private static List<User> setOfUsers(){
        User user2 = new User("deni", "deni", "Deni", "Duhova", "deniduhova@gmail.com", 9794.0);
        User user3 = new User("koko", "koko", "Koko", "Bor", "kbor@gmail.com", 4643.0);
        User user4 = new User("desi", "desi", "Desi", "Popova", "desippv@gmail.com", 8151125.0);
        return List.of(user2,user3, user4);
    }

    private User getOneUser(){
        User user = new User("koko", "koko", "Koko", "Bor", "kbor@gmail.com", 4643.0);
        user.setRoles(Set.of(new Role(Role.ROLE_USER)));
        user.setUserId(1L);
        mockUserRepo.save(user);
        return user;
    }

    private ExpenseCategory getOneCategory(User user){
        ExpenseCategory expenseCategory = new ExpenseCategory("food", user);
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

    private List<ExpenseTransaction> setOfTransactions(User user){
        ExpenseTransaction first = new ExpenseTransaction(1L, LocalDate.parse("2021-12-31"), 90.0, "food",
                "Bought some groceries", user);
        ExpenseTransaction second = new ExpenseTransaction(2L, LocalDate.parse("2021-12-31"), 800.0, "housing",
                "Monthly rent payment.", user);
        ExpenseTransaction third = new ExpenseTransaction(3L, LocalDate.parse("2022-09-02"), 50.0, "subscriptions",
                "Yearly Netflix.", user);
        ExpenseTransaction fourth = new ExpenseTransaction(4L, LocalDate.parse("2022-01-01"), 300.50, "gifts",
                "Gifts for friends", user);
        ExpenseTransaction fifth = new ExpenseTransaction(5L, LocalDate.parse("2021-12-01"), 60.0, "food",
                "Bought some groceries", user);

        List<ExpenseTransaction> res = List.of(first,second,third,fourth, fifth);
        user.setExpenseTransactions(res);
        mockExpenseTransactionRepo.saveAll(res);
        return res;
    }
}