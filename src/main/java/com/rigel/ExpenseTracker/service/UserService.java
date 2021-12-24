package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {

//    whenever we call this method, a user will be saved to the DB
    User saveUser(User user);

//    Saving the category/ies to the user
    ExpenseCategory saveCategory(ExpenseCategory expenseCategory);

    void addExpenseCategory(String username, String categoryName);

    User getUser(String username);

    List<User> getUsers();

    Page<User> getFilteredUsers(Pageable pageable, String username);

    boolean usernameExists(String username);

    Optional<User> getByUsername(String username);

    void deleteUser(String username);
}
