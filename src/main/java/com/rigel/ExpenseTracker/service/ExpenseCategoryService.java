package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ExpenseCategoryService {

    ExpenseCategory saveExpenseCategory(ExpenseCategory category);

    ExpenseTransaction saveTransaction(ExpenseTransaction transaction);

    void addTransaction(String categoryName, Long transactionId);

    Optional<ExpenseCategory> getExpenseCategory(String categoryName);

    Set<ExpenseCategory> getExpenseCategories();

    boolean expenseCategoryExists(String categoryName);

    void deleteExpenseCategory(String categoryName);

    Optional<ExpenseTransaction> getExpenseTransaction(Long id);

    List<ExpenseTransaction> getExpenseTransactions();

    boolean expenseTransactionExists(Long transactionId);

    void deleteExpenseTransactionById(Long transactionId);

    Page<ExpenseTransaction> getFilteredTransactions(Pageable pageable, String categoryName);

    List<ExpenseTransaction> getTransactionsByCategory(String categoryName);

    List<ExpenseTransaction> getTransactionByUser(User user);
}
