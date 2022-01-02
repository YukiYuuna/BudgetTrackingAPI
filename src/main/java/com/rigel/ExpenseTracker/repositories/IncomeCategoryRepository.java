package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.IncomeCategory;
import com.rigel.ExpenseTracker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface IncomeCategoryRepository extends JpaRepository<IncomeCategory, Long> {

    @Query("SELECT DISTINCT(c.categoryName) FROM IncomeCategory c")
    Set<IncomeCategory> findAllCategories();

    Optional<IncomeCategory> findIncomeCategoryByCategoryNameAndUser(String categoryName, User user);

    void deleteIncomeCategoryByUserAndAndCategoryName(User user, String categoryName);
}
