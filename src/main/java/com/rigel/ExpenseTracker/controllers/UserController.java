package com.rigel.ExpenseTracker.controllers;

import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.exception.BadRequestException;
import com.rigel.ExpenseTracker.exception.NotFoundException;
import com.rigel.ExpenseTracker.repositories.*;
import com.sun.istack.Nullable;
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
            throw new NotFoundException("Oops, user with id " + id + " doesn't exist");

        return ResponseEntity.ok(userRepo.findById(id));
    }

    @GetMapping("/filter")
    private ResponseEntity<?> filterUserByFirstAndLastName(String firstName, String lastName, @Nullable Integer currentPage, @Nullable Integer perPage){

        Pageable pageable = createPagination(currentPage, perPage, userRepo.findAll().size());

        Page<User> users = userRepo.filterUsers(pageable, firstName, lastName);
        Map<String, Object> response = new HashMap<>();
        response.put("totalUsers", users.getTotalElements());
        response.put("totalPages", users.getTotalPages());
        response.put("users", users.getContent());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    private ResponseEntity<?> saveUserToDB(@RequestBody User user) {
        String firstName= user.getFirstName();
        String lastName= user.getLastName();
        String email= user.getEmail();
        int age= user.getAge();
        Double currentBudget= user.getCurrentBudget();

        if (firstName != null && lastName != null && email != null && age != 0 && currentBudget != null) {
            userRepo.save(new User(firstName, lastName, email , age, currentBudget));
            return ResponseEntity.ok(firstName + " " + lastName + " has been added successfully!");
        }

        throw new BadRequestException("You should provide all data, including your first and last name, your age, email and current budget!");
    }

    @PutMapping("/modify/{id}")
    public ResponseEntity<?> modifyUserInfo(@RequestBody User updatedUser, @PathVariable Long id){
        try {
            if (!userRepo.existsById(id)) {
                throw new NotFoundException("There is no user with id : " + id);
            }

            return userRepo.findById(id)
                    .map(user -> {
                        user.setFirstName(updatedUser.getFirstName() == null ? user.getFirstName() : updatedUser.getFirstName());
                        user.setLastName(updatedUser.getLastName() == null ? user.getLastName() : updatedUser.getLastName());
                        user.setEmail(updatedUser.getEmail() == null ? user.getEmail() : updatedUser.getEmail());
                        user.setAge(updatedUser.getAge() == 0 ? user.getAge() : updatedUser.getAge());
                        user.setCurrentBudget(updatedUser.getCurrentBudget() == null ? user.getCurrentBudget() : updatedUser.getCurrentBudget());
                        user.setExpenseTransactions(user.getExpenseTransactions() == null ? user.getExpenseTransactions() : updatedUser.getExpenseTransactions());
                        return ResponseEntity.ok(userRepo.save(user));
                    })
                    .orElseGet(() -> {
                        return ResponseEntity.ok(userRepo.save(updatedUser));
                    });
        } catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
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

    static Pageable createPagination(Integer currentPage, Integer perPage, int size) {
        Pageable pageable;
        if((currentPage != null && perPage != null) && (currentPage > 0 && perPage > 0)){
            pageable = PageRequest.of(currentPage - 1, perPage);
        } else if (currentPage == null && perPage == null){
            pageable = PageRequest.of(0, size);
        } else {
            throw new BadRequestException("The value of currentPage and/or perPage parameters cannot be under or equal to 0.");
        }
        return pageable;
    }
}
