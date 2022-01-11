package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.repositories.CategoryRepo;
import com.rigel.ExpenseTracker.repositories.TransactionRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public interface TransactionService {

    void saveCategoryToDB(String categoryName, String type, CategoryRepo repo);

    void saveTransactionToDB(TransactionRepo repo);

    int numberOfTransactionsByCategory(String categoryName, CategoryRepo cRepo, TransactionRepo tRepo);

    Optional<?> getCategory(CategoryRepo repo, String type, String categoryName);

    Set<?> getCategories(String type, TransactionRepo repo);

    HashMap<String, Object> getAllUserTransactions(Pageable pageable, TransactionRepo repo);

    Optional<?> getTransactionById(Long transactionId, TransactionRepo repo);

    Page<?> getTransactionsByCategoryAndUsername(Pageable pageable, String categoryName, CategoryRepo cRepo, TransactionRepo tRepo);

    HashMap<String, Object> getTransactionByDate(String date, TransactionRepo repo);

    Page<?> getTransactions(Pageable pageable, TransactionRepo repo);

    void addCategory(String categoryName, CategoryRepo repo);

    void addTransaction(String date, Double TransactionAmount, String categoryName, String description, TransactionRepo tRepo, CategoryRepo cRepo);

    boolean transactionExists(Long transactionId, TransactionRepo repo);

    boolean categoryExists(String categoryName, CategoryRepo repo);

    void deleteTransactionByUser(TransactionRepo repo);

    void deleteTransactionById(Long transactionId, TransactionRepo repo);

    void deleteTransactionsByCategory(String categoryName,CategoryRepo cRepo, TransactionRepo tRepo);

    void deleteCategory(String categoryName, CategoryRepo repo);

    String getUsernameByAuthentication();

    Optional<User> getUser();
}