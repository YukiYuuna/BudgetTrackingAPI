package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.TransactionCategory;
import com.rigel.ExpenseTracker.entities.User;
import java.util.Optional;
import java.util.Set;

public interface CategoryRepo {
    Set<?> findAllCategories();
//    Set<TransactionCategory> findMappedCategories();
    Optional<?> findCategoryByNameAndUser(String categoryName, User user);
}
