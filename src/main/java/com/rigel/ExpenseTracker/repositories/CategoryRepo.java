package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.User;
import java.util.Optional;
import java.util.Set;

public interface CategoryRepo {
    Set<?> findAllCategories();
    Optional<?> fetchCategoryByCategoryNameAndUser(String categoryName, User user);
}
