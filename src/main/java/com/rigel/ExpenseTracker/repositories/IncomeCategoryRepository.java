package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.IncomeCategory;
import com.rigel.ExpenseTracker.entities.TransactionCategory;
import com.rigel.ExpenseTracker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface IncomeCategoryRepository extends JpaRepository<IncomeCategory, Long>, CategoryRepo {

    @Override
    @Query("SELECT c FROM IncomeCategory c")
    Set<IncomeCategory> findAllCategories();

//    @Override
//    @Query("SELECT c FROM IncomeCategory c WHERE c.categoryType = 'income' ")
//    Set<TransactionCategory> findMappedCategories();

    @Override
    @Query("SELECT c FROM IncomeCategory c WHERE c.categoryName = ?1 AND c.user = ?2")
    Optional<IncomeCategory> findCategoryByNameAndUser(String categoryName, User user);

    void deleteIncomeCategoryByUserAndAndCategoryName(User user, String categoryName);
}
