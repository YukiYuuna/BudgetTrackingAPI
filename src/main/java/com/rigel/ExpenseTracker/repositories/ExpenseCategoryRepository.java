package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface ExpenseCategoryRepository  extends JpaRepository<ExpenseCategory, Long> {

    @Query("SELECT c FROM ExpenseCategory c")
    Set<ExpenseCategory> findAllCategories();

    ExpenseCategory findExpenseCategoryById(Long id);

    Optional<ExpenseCategory> findExpenseCategoryByCategoryName(String categoryName);

    Boolean existsByCategoryName(String categoryName);

}
