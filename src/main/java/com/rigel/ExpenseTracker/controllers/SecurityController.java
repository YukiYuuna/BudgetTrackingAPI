package com.rigel.ExpenseTracker.controllers;

import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.exception.BadRequestException;
import com.rigel.ExpenseTracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SecurityController {

    private final UserService userService;

    @GetMapping("/refresh/token}")
    private ResponseEntity<?> getRefreshToken(){

    }

    @PostMapping("/register")
    private ResponseEntity<String> registerUser(String username, String password, String firstName, String lastName, String email, Double currentBudget) {
        if (username !=  null && password != null && firstName != null && lastName != null && email != null && currentBudget != null) {
            userService.saveUser(new User(username, password, firstName, lastName, email, currentBudget));
            return ResponseEntity.ok().body(firstName + " " + lastName + " has been added successfully!");
        }

        throw new BadRequestException("Make sure you provide all data, including: username, password, first name, last name and current budget!");
    }




}
