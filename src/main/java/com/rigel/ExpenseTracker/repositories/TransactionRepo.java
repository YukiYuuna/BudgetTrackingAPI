package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.TransactionCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepo {
    List<?> fetchTransactionsByCategory(String name);
    Optional<?> fetchTransactionsById(Long id);
    List<TransactionCategory> findMappedTransactions();
    Page<?> filteredTransactionsByDate(Pageable pageable,String username, LocalDate date);
    Optional<?> filteredTransactionsById(Pageable pageable,Long transactionId, String username);
    Page<?> filteredTransactions(Pageable pageable);
    Page<?> filterTransactionsByUsername(Pageable pageable, String username);
    Page<?> filterTransactionsByUsernameAndCategory(Pageable pageable, String username,  String categoryName);
}
