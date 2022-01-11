package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IncomeTransactionRepository extends JpaRepository<IncomeTransaction, Long>, TransactionRepo{

    @Override
    @Query("SELECT i "
            + "FROM IncomeTransaction i "
            + "WHERE i.categoryType = 'income'")
    List<TransactionCategory> findMappedTransactions();

    @Override
    @Query("SELECT e "
            + "FROM IncomeTransaction e "
            + "WHERE e.categoryName = ?1")
    List<IncomeTransaction> findTransactionsByCategoryName(String name);

    @Override
    @Query("SELECT e "
            + "FROM IncomeTransaction e "
            + "WHERE e.incomeTransactionId = ?1")
    Optional<IncomeTransaction> findTransactionsByTransactionId(Long id);

    @Override
    @Query("SELECT e "
            + "FROM IncomeTransaction e")
    Page<IncomeTransaction> filteredTransactions(Pageable pageable);

    @Override
    @Query("SELECT e "
            + "FROM IncomeTransaction e "
            + "WHERE "
            + "lower(e.user.username) "
            + "LIKE :#{#username == null || #username.isEmpty()? '%' : #username + '%'} ")
    Page<IncomeTransaction> filterTransactionsByUsername(Pageable pageable, String username);

    @Override
    @Query("SELECT e "
            + "FROM IncomeTransaction e "
            + "WHERE "
            + "lower(e.user.username) "
            + "LIKE :#{#username == null || #username.isEmpty()? '%' : #username + '%'} "
            + "AND "
            + "lower(e.categoryName) "
            + "LIKE :#{#categoryName == null || #categoryName.isEmpty()? '%' : #categoryName + '%'} ")
    Page<IncomeTransaction> filterTransactionsByUsernameAndCategory(Pageable pageable, String username,  String categoryName);

    boolean existsIncomeTransactionByUserAndIncomeTransactionId(User user, Long id);

    void deleteIncomeTransactionsByIncomeCategoryAndUser(IncomeCategory category, User user);

    void deleteIncomeTransactionsByUser(User user);

}
