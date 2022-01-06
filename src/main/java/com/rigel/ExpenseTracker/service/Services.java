package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public abstract class Services {

    String getUsernameByAuthentication(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    abstract Optional<User> userExists(String username);

    abstract Object doesCategoryExist(User user, String categoryName);
}
