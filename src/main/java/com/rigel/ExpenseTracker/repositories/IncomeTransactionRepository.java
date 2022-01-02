package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.IncomeCategory;
import com.rigel.ExpenseTracker.entities.IncomeTransaction;
import com.rigel.ExpenseTracker.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IncomeTransactionRepository extends JpaRepository<IncomeTransaction, Long> {

    List<IncomeTransaction> findIncomeTransactionByIncomeCategory_CategoryName(String name);

    boolean existsIncomeTransactionByUserAndIncomeTransactionId(User user, Long id);

    IncomeTransaction findIncomeTransactionsByIncomeTransactionId(Long id);

    void deleteIncomeTransactionsByIncomeCategoryAndUser(IncomeCategory category, User user);

    @Query("SELECT e "
            + "FROM IncomeTransaction e")
    Page<IncomeTransaction> filteredTransactions(Pageable pageable);

    @Query("SELECT e "
            + "FROM IncomeTransaction e "
            + "WHERE "
            + "lower(e.user.username) "
            + "LIKE :#{#username == null || #username.isEmpty()? '%' : #username + '%'} ")
    Page<IncomeTransaction> filterTransactionsByUsername(Pageable pageable, String username);

    @Query("SELECT e "
            + "FROM IncomeTransaction e "
            + "WHERE "
            + "lower(e.user.username) "
            + "LIKE :#{#username == null || #username.isEmpty()? '%' : #username + '%'} "
            + "AND "
            + "lower(e.categoryName) "
            + "LIKE :#{#categoryName == null || #categoryName.isEmpty()? '%' : #categoryName + '%'} ")
    Page<IncomeTransaction> filterTransactionsByUsernameAndCategory(Pageable pageable, String username, String categoryName);

    void deleteIncomeTransactionsByUser(User user);
}
