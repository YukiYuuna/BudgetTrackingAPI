package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.Role;
import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public interface UserService {

    User saveUser(User user);

    void clearSave(User user);

    Role saveRole(Role role);

    void addRoleToUser(String roleName);

    User getUser();

    Optional<User> getOptionalUser();

    int numberOfUsers();

    User getUserById(Long userId);

    Page<User> getUsers(Pageable pageable);

    List<User> getAllDBUsers();

    boolean usernameExists();

    void deleteUser();

    void saveUserDataAndFlush(User user);

    String getUsernameByAuthentication();
}
