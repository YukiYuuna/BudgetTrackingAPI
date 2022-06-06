package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.IncomeCategory;
import com.rigel.ExpenseTracker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface IncomeCategoryRepository extends JpaRepository<IncomeCategory, Long> {

    Set<IncomeCategory> findIncomeCategoriesByUser(User user);

    Optional<IncomeCategory> findIncomeCategoryByIncomeCategoryIdAndUser(Long incomeCategoryId, User user);

    Optional<IncomeCategory> findIncomeCategoryByCategoryNameAndUser(String name, User user);

    boolean existsIncomeCategoryByIncomeCategoryIdAndUser(Long incomeCategoryId, User user);

    boolean existsIncomeCategoryByCategoryNameAndUser(String categoryName, User user);

    void deleteIncomeCategoryByUserAndIncomeCategoryId(User user, Long incomeCategoryId);
}
