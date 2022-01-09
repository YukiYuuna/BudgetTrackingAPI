package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public interface TransactionService {

    void saveTransactionCategoryToDB(String categoryName, TransactionService service);

    void saveTransactionTransactionToDB(Optional<?> transaction, TransactionService service);

    int numberOfTransactionsByCategory(String categoryName, TransactionService service);

    Optional<?> getOptionalTransactionCategory(String categoryName);

    Optional<?> getTransactionCategory(String categoryName, TransactionRepo repo);

    public Set<?> getTransactionCategories(TransactionRepo repo);

    HashMap<String, Object> getAllUserTransactions(Pageable pageable, TransactionService service);

    Optional<?> getTransactionById(Long transactionId, TransactionService service);

    Page<?> getTransactionsByCategoryAndUsername(Pageable pageable, String categoryName, TransactionService service);

    HashMap<String, Object> getTransactionTransactionByDate(String date, TransactionService service);

    Page<?> getTransactionTransactions(Pageable pageable, TransactionService service);

    void addTransactionCategory(String categoryName, TransactionService service);

    void addTransactionTransaction(String date, Double TransactionAmount, String categoryName, String description, TransactionService service);

    boolean TransactionTransactionExists(Long transactionId, TransactionService service);

    boolean TransactionCategoryExists(String categoryName, TransactionService service);

    void deleteTransactionByUser(TransactionService service);

    void deleteTransactionById(Long transactionId, TransactionService service);

    void deleteTransactionsByCategory(String categoryName, TransactionService service);

    void deleteTransactionCategory(String categoryName, TransactionService service);

    String getUsernameByAuthentication();

    Optional<User> getUser();

    Object doesCategoryExist(User user, String categoryName);
}