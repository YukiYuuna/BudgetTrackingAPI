package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface ExpenseCategoryRepository  extends JpaRepository<ExpenseCategory, Long> {

    @Query("SELECT c FROM ExpenseCategory c")
    Set<ExpenseCategory> findAllCategories();

    ExpenseCategory findExpenseCategoryById(Long id);

    Boolean existsExpenseCategoryByCategoryName(String name);

    ExpenseCategory findExpenseCategoryByCategoryName(String name);

}
