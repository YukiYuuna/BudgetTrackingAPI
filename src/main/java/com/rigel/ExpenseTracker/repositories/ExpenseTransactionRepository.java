package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface ExpenseTransactionRepository extends JpaRepository<ExpenseTransaction, Long> {

    List<ExpenseTransaction> findExpenseTransactionByExpenseCategory_CategoryName(String name);

    List<ExpenseTransaction> findAllByUser(User user);

    ExpenseTransaction findExpenseTransactionById(Long id);

    List<ExpenseTransaction> findAllByDate(LocalDate date);


}
