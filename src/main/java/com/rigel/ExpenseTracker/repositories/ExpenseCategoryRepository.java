package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {

    @Query("SELECT c FROM ExpenseCategory c WHERE c.user.username = ?2")
    Set<ExpenseCategory> findAllUserCategories(String username);

    @Query("SELECT c FROM IncomeCategory c WHERE c.categoryName = ?1 AND c.user = ?2")
    Optional<ExpenseCategory> fetchCategoryByCategoryNameAndUser(String categoryName, User user);

    boolean existsExpenseCategoryByCategoryNameAndUser(String categoryName, User user);

    void deleteExpenseCategoryByUserAndCategoryName(User user, String categoryName);
}
