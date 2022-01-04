package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface ExpenseCategoryRepository  extends JpaRepository<ExpenseCategory, Long> {

    @Query("SELECT DISTINCT(c.categoryName) FROM ExpenseCategory c")
    Set<ExpenseCategory> findAllCategories();

    Optional<ExpenseCategory> findExpenseCategoryByCategoryNameAndUser(String categoryName, User user);

    void deleteExpenseCategoryByUserAndAndCategoryName(User user, String categoryName);

}
