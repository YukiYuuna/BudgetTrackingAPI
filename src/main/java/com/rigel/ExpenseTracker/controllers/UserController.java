package com.rigel.ExpenseTracker.controllers;

import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.repositories.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepo;

    public UserController(UserRepository userRepository){
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
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userRepo.findUserById(id));
    }

    @GetMapping("/filter")
    private ResponseEntity<?> filterUserByFirstAndLastName(String firstName, String lastName, int currentPage, int perPage){
       Pageable pageable = PageRequest.of(currentPage - 1, perPage);
        Page<User> users = userRepo.filterUsers(pageable, firstName, lastName);
        Map<String, Object> response = new HashMap<>();
        response.put("totalUsers", users.getTotalElements());
        response.put("totalPages", users.getTotalPages());
        response.put("users", users.getContent());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    private ResponseEntity<?> saveUserToDB(String fName, String lName, String email, Integer age, Double currentBudget) {
        if (fName != null && lName != null && email != null && age != null && currentBudget != null) {
            userRepo.save(new User(fName, lName, email, age, currentBudget));
            return ResponseEntity.ok(fName + " " + lName + " has been added successfully!");
        }
        return ResponseEntity.ok("You should provide all data, including your first and last name, your age, email and current budget!");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteByFirstAndLastName(String fName, String lName) {
        if (!(userRepo.existsByFirstNameAndLastName(fName, lName)))
            return ResponseEntity.notFound().build();

        User user = userRepo.findUserByFirstNameAndLastName(fName, lName);
        userRepo.delete(user);
        return ResponseEntity.ok("User wa deleted successfully!");
    }

    @DeleteMapping("delete/{id}")
    private ResponseEntity<?> deleteUserById(@PathVariable Long id){
        if(!(userRepo.existsById(id))) {
            return ResponseEntity.notFound().build();
        }
        userRepo.deleteById(id);
        return ResponseEntity.ok("The user has been deleted successfully!");
    }
}
