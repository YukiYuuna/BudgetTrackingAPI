package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.IncomeCategory;
import com.rigel.ExpenseTracker.entities.IncomeTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public interface IncomeService {
    void saveIncomeCategoryToDB(String categoryName);

    void saveIncomeTransactionToDB(IncomeTransaction transaction);

    int numberOfTransactionsByCategory(String categoryName);

    Optional<IncomeCategory> getOptionalIncomeCategory(String categoryName);

    IncomeCategory getIncomeCategory(String categoryName);

    Set<IncomeCategory> getIncomeCategories();

    HashMap<String, Object> getAllUserTransactions(Pageable pageable);

    Optional<IncomeTransaction> getTransactionById(Long transactionId);

    Page<IncomeTransaction> getTransactionsByCategoryAndUsername(Pageable pageable, String categoryName);

    HashMap<String, Object> getIncomeTransactionByDate(String date);

    Page<IncomeTransaction> getIncomeTransactions(Pageable pageable);

    void addIncomeCategory(String categoryName);

    void addIncomeTransaction(String date, Double incomeAmount, String categoryName, String description);

    boolean incomeTransactionExists(Long transactionId);

    boolean incomeCategoryExists(String categoryName);

    void deleteTransactionByUser();

    void deleteTransactionById(Long transactionId);

    void deleteTransactionsByCategory(String categoryName);

    void deleteIncomeCategory(String categoryName);

}
