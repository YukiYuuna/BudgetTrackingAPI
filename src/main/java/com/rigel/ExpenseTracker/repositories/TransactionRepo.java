package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.TransactionCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TransactionRepo {
    List<?> findTransactionsByCategoryName(String name);
    Optional<?> findTransactionsByTransactionId(Long id);
    List<TransactionCategory> findMappedTransactions();
    Page<?> filteredTransactions(Pageable pageable);
    Page<?> filterTransactionsByUsername(Pageable pageable, String username);
    Page<?> filterTransactionsByUsernameAndCategory(Pageable pageable, String username,  String categoryName);
}
