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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
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
        ExpenseCategory expenseCategory = new ExpenseCategory("food", user);
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
        ExpenseTransaction transaction = new ExpenseTransaction(LocalDate.parse("2021-12-31"), 90.0, "food",
                "Bought some groceries", user);
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
    }

    @Test
    void getCategory() {
    }

    @Test
    void getCategories() {
    }

    @Test
    void getTransactionById() {
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
        User user = new User("ivan", "ivan", "Ivan", "Duhov", "iduhov@gmail.com", 10000.0);
        user.setRoles(Set.of(new Role(Role.ROLE_USER)));
        mockUserRepo.save(user);
        return user;
    }
}