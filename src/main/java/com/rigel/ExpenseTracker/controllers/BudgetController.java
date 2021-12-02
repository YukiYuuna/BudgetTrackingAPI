package com.rigel.ExpenseTracker.controllers;

import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.repositories.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class BudgetController {

    private final UserRepository userRepo;

    public BudgetController(UserRepository userRepository){
        this.userRepo = userRepository;
    }

    @GetMapping("/fetch")
    private ResponseEntity<?> fetchAllUsers(){
        if(userRepo.findAll().size() == 0){
            return ResponseEntity.ok("There are currently no saved users.");
        }
        return ResponseEntity.ok(userRepo.findAll());
    }

    @GetMapping("/fetch/{id}")
    private ResponseEntity<?> fetchUserById(@PathVariable Long id){
        if(!(userRepo.existsById(id))){
            return ResponseEntity.ok("The user doesn't exist!");
        }
        return ResponseEntity.ok(userRepo.findUserById(id));
    }

    @PostMapping("/save")
    private ResponseEntity<?> saveUserToDB(String fName, String lName, String email, Integer age, Double currentBudget) {
        if (fName != null && lName != null && email != null && age != null && currentBudget != null) {
            userRepo.save(new User(fName, lName, email, age, currentBudget));
            return ResponseEntity.ok(fName + " " + lName + " has been added successfully!");
        }
        return ResponseEntity.ok("You should provide all data, including your first and last name, your age, email and current budget!");
    }

    @DeleteMapping("delete/user/{id}")
    private ResponseEntity<?> deleteUserById(@PathVariable Long id){
        if(!(userRepo.existsById(id))){
            return ResponseEntity.ok("The user doesn't exist!");
        }
        userRepo.deleteById(id);
        return ResponseEntity.ok("The user has been deleted successfully!");
    }
}
