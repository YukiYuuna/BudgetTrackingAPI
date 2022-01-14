package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.IncomeCategory;
import com.rigel.ExpenseTracker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface IncomeCategoryRepository extends JpaRepository<IncomeCategory, Long> {

    @Query("SELECT c FROM IncomeCategory c WHERE c.categoryType = ?1 AND c.user.username = ?2")
    Set<IncomeCategory> findAllUserCategories(String type, String username);

    @Query("SELECT c FROM IncomeCategory c WHERE c.categoryName = ?1 AND c.user = ?2")
    Optional<IncomeCategory> fetchCategoryByCategoryNameAndUser(String categoryName, User user);

    boolean existsIncomeCategoryByCategoryNameAndUser(String categoryName, User user);

    void deleteIncomeCategoryByUserAndCategoryName(User user, String categoryName);
}
