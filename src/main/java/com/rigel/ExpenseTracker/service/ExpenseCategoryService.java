package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ExpenseCategoryService {

    void saveExpenseCategoryToDB(ExpenseCategory category);

    void saveExpenseTransactionToDB(ExpenseTransaction transaction);

    Optional<ExpenseCategory> getOptionalExpenseCategory(String categoryName);

    ExpenseCategory getExpenseCategory(String categoryName);

    Set<ExpenseCategory> getExpenseCategories();

    HashMap<String, Object> getAllUserTransactions(Pageable pageable);

    Optional<ExpenseTransaction> getTransactionById(Long transactionId);

    Page<ExpenseTransaction> getTransactionsByCategoryAndUsername(Pageable pageable, String categoryName, String username);

    HashMap<String, Object> getExpenseTransactionByDate(String date);

    Page<ExpenseTransaction> getExpenseTransactions(Pageable pageable);

    void addExpenseCategory(String categoryName);

    void addExpenseTransaction(String date, Double expenseAmount, String categoryName, String description);

    boolean expenseTransactionExists(Long transactionId);

    boolean expenseCategoryExists(String categoryName);

    void deleteTransactionByUser();

    void deleteTransactionById(Long transactionId);

    void deleteTransactionsByCategory(String categoryName);

    void deleteExpenseCategory(String categoryName);

}
