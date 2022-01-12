package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.repositories.CategoryRepo;
import com.rigel.ExpenseTracker.repositories.TransactionRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public interface TransactionService {

//    done
    void saveCategoryToDB(String categoryName, String type);

//    done
    void saveTransactionToDB(LocalDate date, Double expenseAmount, String categoryName, String description, String categoryType);

    int numberOfTransactionsByCategory(String categoryName, CategoryRepo cRepo, TransactionRepo tRepo);

//    done
    Optional<?> getCategory(String type, String categoryName);

//    done
    Set<?> getCategories(String type);

//    done
    Optional<?> getTransactionById(Long transactionId, TransactionRepo repo);

//    done
    Page<?> getTransactionsByCategoryAndUsername(Pageable pageable, String type,
                                                 String categoryName, CategoryRepo cRepo,
                                                 TransactionRepo tRepo);

//    done
    Page<?> getTransactionByDate(Pageable pageable, String date, String type);

//    done
    Page<?> getAllUserTransactions(Pageable pageable, String type, TransactionRepo repo);

//    done
    void addCategory(String categoryName, String type, CategoryRepo repo);

//    done
    void addTransaction(String categoryType,String date, Double TransactionAmount, String categoryName, String description, TransactionRepo tRepo, CategoryRepo cRepo);

    boolean transactionExists(Long transactionId, TransactionRepo repo);

    boolean categoryExists(String categoryName, CategoryRepo repo);

    void deleteTransactionByUser(TransactionRepo repo);

    void deleteTransactionById(Long transactionId, TransactionRepo repo);

    void deleteTransactionsByCategory(String categoryName,CategoryRepo cRepo, TransactionRepo tRepo);

    void deleteCategory(String categoryName, CategoryRepo repo);

    String getUsernameByAuthentication();

    Optional<User> getUser();
}