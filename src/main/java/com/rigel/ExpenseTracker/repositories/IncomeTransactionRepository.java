package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.IncomeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeTransactionRepository extends JpaRepository<IncomeTransaction, Long> {
}
