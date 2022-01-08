package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExpenseTransactionRepository extends JpaRepository<ExpenseTransaction, Long> {

    boolean existsExpenseTransactionByUserAndExpenseTransactionId(User user, Long id);

    void deleteExpenseTransactionsByExpenseCategoryAndUser(ExpenseCategory category, User user);

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

    void deleteExpenseTransactionsByUser(User user);

}
