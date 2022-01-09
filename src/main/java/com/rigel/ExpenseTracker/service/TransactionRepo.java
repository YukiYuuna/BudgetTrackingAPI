package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.TransactionCategory;
import com.rigel.ExpenseTracker.entities.User;
import java.util.Optional;
import java.util.Set;

public interface TransactionRepo {

    Set<TransactionCategory> findAllCategories();

    Optional<?> findExpenseCategoryByCategoryNameAndUser(String categoryName, User user);

    void deleteExpenseCategoryByUserAndAndCategoryName(User user, String categoryName);
}
