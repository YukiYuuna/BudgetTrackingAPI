package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.TransactionCategory;
import com.rigel.ExpenseTracker.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseTransactionRepository extends JpaRepository<ExpenseTransaction, Long> {

    @Query("SELECT e "
            + "FROM ExpenseTransaction e "
            + "WHERE e.categoryType = 'expense'")
    List<TransactionCategory> findMappedTransactions();

    @Query("SELECT e "
            + "FROM ExpenseTransaction e "
            + "WHERE e.categoryName = ?1")
    List<ExpenseTransaction> fetchTransactionsByCategory(String categoryName);

    @Query("SELECT e "
            + "FROM ExpenseTransaction e")
    Page<ExpenseTransaction> filteredTransactions(Pageable pageable);

    @Query("SELECT e "
            + "FROM ExpenseTransaction e "
            + "WHERE "
            + "lower(e.user.username) "
            + "LIKE :#{#username == null || #username.isEmpty()? '%' : #username + '%'} ")
    Page<ExpenseTransaction> filterTransactionsByUsername(Pageable pageable, String username);

    @Query("SELECT e "
            + "FROM ExpenseTransaction e "
            + "WHERE "
            + "lower(e.user.username) "
            + "LIKE :#{#username == null || #username.isEmpty()? '%' : #username + '%'} "
            + "AND "
            + "lower(e.categoryName) "
            + "LIKE :#{#categoryName == null || #categoryName.isEmpty()? '%' : #categoryName + '%'} ")
    Page<ExpenseTransaction> filterTransactionsByUsernameAndCategory(Pageable pageable, String username,  String categoryName);

    @Query("SELECT e "
            + "FROM ExpenseTransaction e "
            + "WHERE e.date = ?2 "
            + "AND e.user.username = ?1")
    Page<ExpenseTransaction> filteredTransactionsByDate(Pageable pageable, String username, LocalDate date);

    boolean existsExpenseTransactionByUserAndExpenseTransactionId(User user, Long id);

    void deleteExpenseTransactionsByExpenseCategoryAndUser(ExpenseCategory category, User user);

    void deleteExpenseTransactionsByUser(User user);

}
