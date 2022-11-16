package com.rigel.ExpenseTracker.controllers;

import com.rigel.ExpenseTracker.service.UserService;
import com.rigel.ExpenseTracker.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Slf4j
public class SecurityController {

    private final UserService userService;

    @Autowired
    public SecurityController(@Lazy UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String email = user.getEmail();
        Double currentBudget = user.getCurrentBudget();
        if (username != null && (password != null || !password.isEmpty()) && firstName != null && lastName != null && email != null && currentBudget != null) {
            boolean invalidUsername = userService.getAllDBUsers().stream().anyMatch(u -> u.getUsername().equals(username));
            boolean invalidEmail = userService.getAllDBUsers().stream().anyMatch(u -> u.getEmail().equals(email));
            if (!invalidUsername && !invalidEmail) {

                userService.saveUser(new User(username, password, firstName, lastName, email, currentBudget));
                return ResponseEntity.ok().body(firstName + " " + lastName + " has been added successfully!");
            } else {
                if (invalidUsername) {
                    throw new ResponseStatusException(BAD_REQUEST, "User with this username already exists.");
                }
                throw new ResponseStatusException(BAD_REQUEST, "User with this email already exists.");
            }
        }
        throw new ResponseStatusException(BAD_REQUEST, "Make sure you provide all data, including: username, password, first and last name, email and current budget!");
    }


}
