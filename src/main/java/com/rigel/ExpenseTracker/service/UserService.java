package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.Role;
import com.rigel.ExpenseTracker.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User saveUser(User user);

    Integer numberOfUsers();

    Role saveRole(Role role);

    void addRoleToUser(String password);

    void addExpenseCategory(String categoryName);

    User getUser();

    Page<User> getUsers(Pageable pageable);

    boolean usernameExists();

    Optional<User> getOptionalUser();

    void deleteUser();

    void saveUserDataAndFlush(User user);
}
