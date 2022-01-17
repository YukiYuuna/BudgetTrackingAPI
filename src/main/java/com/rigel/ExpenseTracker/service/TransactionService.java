package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.User;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;

public interface TransactionService {

//    done
    void saveCategoryToDB(String categoryName, String type);

//    done
    void saveTransactionToDB(LocalDate date, Double expenseAmount, String categoryName, String description, String categoryType);

//    done
    int numberOfTransactionsByCategory(String type, String categoryName);

//    done
    Optional<?> getCategory(String type, String categoryName);

//    done
    HashMap<String, Object> getCategories(String type);

//    done
    Optional<?> getTransactionById(String type, Long transactionId);

//    done
    HashMap<String, Object> getTransactionsByCategoryAndUsername(Pageable pageable, String type, String categoryName);

//    done
    HashMap<String, Object> getTransactionByDate(Pageable pageable, String date, String type);

//    done
    HashMap<String, Object> getAllUserTransactions(Pageable pageable, String type);

//    done
    void addCategory(String categoryName, String type);

//    done
    void addTransaction(String categoryType,String date, Double TransactionAmount, String categoryName, String description);

//    done
    boolean transactionExists(String type, Long transactionId);

//    done
    boolean categoryExists(String type, String categoryName);

//    done
    void deleteAllUserTransactions(String type);

//    done
    void deleteTransactionById(String type, Long transactionId);

//    done
    void deleteTransactionsByCategory(String type, String categoryName);

//    done
    void deleteCategory(String categoryName, String type);
}