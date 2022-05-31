package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {

    Set<ExpenseCategory> findExpenseCategoriesByUser(User user);

    Optional<ExpenseCategory> findExpenseCategoryByExpenseCategoryIdAndUser(Long expenseCategoryId, User user);

    Optional<ExpenseCategory> findExpenseCategoryByCategoryNameAndUser(String name, User user);

    boolean existsExpenseCategoryByExpenseCategoryIdAndUser(Long expenseCategoryId, User user);

    boolean existsExpenseCategoryByCategoryNameAndUser(String categoryName, User user);

    void deleteExpenseCategoryByUserAndExpenseCategoryId(User user, Long expenseCategoryId);
}
