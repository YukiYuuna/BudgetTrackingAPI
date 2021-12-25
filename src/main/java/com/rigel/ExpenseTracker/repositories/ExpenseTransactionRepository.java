package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseTransactionRepository extends JpaRepository<ExpenseTransaction, Long> {

    List<ExpenseTransaction> findExpenseTransactionByExpenseCategory_CategoryName(String name);

    ExpenseTransaction findExpenseTransactionById(Long id);

    List<ExpenseTransaction> findAllByDate(LocalDate date);

    List<ExpenseTransaction> findExpenseTransactionByUser(User user);

    @Query("SELECT e "
            + "FROM ExpenseTransaction e "
            + "WHERE "
            + "lower(e.category) "
            + "LIKE :#{#category == null || #category.isEmpty()? '%' : #category + '%'} ")
    Page<ExpenseTransaction> filterTransactionsByCategory(Pageable pageable, String category);

}
