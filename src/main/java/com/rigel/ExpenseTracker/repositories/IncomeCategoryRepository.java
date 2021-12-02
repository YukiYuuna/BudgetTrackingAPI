package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.IncomeCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeCategoryRepository extends JpaRepository<IncomeCategory, Long> {

}
