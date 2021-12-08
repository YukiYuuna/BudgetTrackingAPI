package com.rigel.ExpenseTracker.controllers;

import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.exception.BadRequestException;
import com.rigel.ExpenseTracker.exception.NotFoundException;
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
        return ResponseEntity.ok(userRepo.findAll());
    }

    @GetMapping("/fetch/{id}")
    private ResponseEntity<?> fetchById(@PathVariable Long id){
        if(!(userRepo.existsById(id)))
            throw new NotFoundException("Oops, user with id:" + id + " doesn't exist");

        return ResponseEntity.ok(userRepo.findById(id));
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
        throw new BadRequestException("You should provide all data, including your first and last name, your age, email and current budget!");
    }

    @PutMapping("/modify/{id}")
    public ResponseEntity<?> modifyUserInfo(@RequestBody User updatedUser, @PathVariable Long id){
        if(!userRepo.existsById(id)){
            throw new NotFoundException("There is no user with id : " + id);
        }

        return userRepo.findById(id)
                .map(user -> {
                    user.setFirstName(updatedUser.getFirstName());
                    user.setLastName(updatedUser.getLastName());
                    user.setEmail(updatedUser.getEmail());
                    user.setAge(updatedUser.getAge());
                    user.setCurrentBudget(updatedUser.getCurrentBudget());
                    user.setExpenseTransactions(user.getExpenseTransactions());
                    return ResponseEntity.ok(userRepo.save(user));
                })
                .orElseGet(() -> {
                    return ResponseEntity.ok(userRepo.save(updatedUser));
                });
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteByFirstAndLastName(String firstName, String lastName) {
        if (!(userRepo.existsByFirstNameAndLastName(firstName, lastName)))
           throw new NotFoundException("User with name: " + firstName + " " + lastName + " doesn't exist.");

        User user = userRepo.findUserByFirstNameAndLastName(firstName, lastName);
        userRepo.delete(user);
        return ResponseEntity.ok("User was deleted successfully!");
    }

    @DeleteMapping("delete/{id}")
    private ResponseEntity<?> deleteUserById(@PathVariable Long id){
        if(!(userRepo.existsById(id)))
            throw new NotFoundException("User with id: "+ id + " doesn't exist.");

        userRepo.deleteById(id);
        return ResponseEntity.ok("The user has been deleted successfully!");
    }
}
