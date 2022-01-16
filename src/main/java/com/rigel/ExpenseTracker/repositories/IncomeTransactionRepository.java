package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IncomeTransactionRepository extends JpaRepository<IncomeTransaction, Long>{

    @Query("SELECT i "
            + "FROM IncomeTransaction i "
            + "WHERE i.categoryName = ?1")
    List<IncomeTransaction> fetchTransactionsByCategory(String name);

    @Query("SELECT i "
            + "FROM IncomeTransaction i")
    Page<IncomeTransaction> filteredTransactions(Pageable pageable);

    @Query("SELECT i "
            + "FROM IncomeTransaction i "
            + "WHERE "
            + "lower(i.user.username) "
            + "LIKE :#{#username == null || #username.isEmpty()? '%' : #username + '%'} ")
    Page<IncomeTransaction> filterTransactionsByUsername(Pageable pageable, String username);

    @Query("SELECT i "
            + "FROM IncomeTransaction i "
            + "WHERE "
            + "lower(i.user.username) "
            + "LIKE :#{#username == null || #username.isEmpty()? '%' : #username + '%'} "
            + "AND "
            + "lower(i.categoryName) "
            + "LIKE :#{#categoryName == null || #categoryName.isEmpty()? '%' : #categoryName + '%'} ")
    Page<IncomeTransaction> filterTransactionsByUsernameAndCategory(Pageable pageable, String username,  String categoryName);

    @Query("SELECT i "
            + "FROM IncomeTransaction i "
            + "WHERE i.user.username = ?1 "
            + "AND i.date = ?2")
    Page<IncomeTransaction> filteredTransactionsByDate(Pageable pageable, String username, LocalDate date);

    boolean existsIncomeTransactionByUserAndIncomeTransactionId(User user, Long id);

    void deleteIncomeTransactionsByIncomeCategoryAndUser(IncomeCategory category, User user);

    void deleteIncomeTransactionsByUser(User user);

}
