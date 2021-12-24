package com.rigel.ExpenseTracker.controllers;

import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.exception.BadRequestException;
import com.rigel.ExpenseTracker.exception.NotFoundException;
import com.rigel.ExpenseTracker.service.UserService;
import com.sun.istack.Nullable;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Shows all users of the expense tracker app.
     * @return all users in the app.
     */
    @GetMapping("/users")
    private ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @GetMapping("/user/{username}")
    private ResponseEntity<User> getByUsername(@PathVariable String username){
        if(!(userService.usernameExists(username)))
            throw new NotFoundException("Oops, user with this username doesn't exist");

        return ResponseEntity.ok().body(userService.getUser(username));
    }

    @GetMapping("/users/filter")
    @ApiOperation(value = "Filters the users by username, using pagination.",
    notes = "Provide the username, the current page and how many users you want per page, in order to get a response.",
    response = ResponseEntity.class)
    private ResponseEntity<Map<String, Object>> filterUserByFirstAndLastName(String username, @Nullable Integer currentPage, @Nullable Integer perPage){

        Pageable pageable = createPagination(currentPage, perPage, userService.getUsers().size());

        Page<User> users = userService.getFilteredUsers(pageable, username);
        Map<String, Object> response = new HashMap<>();
        response.put("totalUsers", users.getTotalElements());
        response.put("totalPages", users.getTotalPages());
        response.put("users", users.getContent());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/user/save")
    private ResponseEntity<String> saveUser(String username, String firstName, String lastName, String email, Double currentBudget) {
        if (username !=  null && firstName != null && lastName != null && email != null && currentBudget != null) {
            userService.saveUser(new User(username, firstName, lastName, email, currentBudget));
            return ResponseEntity.ok().body(firstName + " " + lastName + " has been added successfully!");
        }

        throw new BadRequestException("Make sure you provide all data, including: username, first and last name, current budget!");
    }

    @PutMapping("/modify/{username}")
    public Optional<User> modifyUserInfo(@RequestBody User updatedUser, @PathVariable String username) {
        if (!userService.usernameExists(username)) {
            throw new NotFoundException("User with this username doesn't exists.");
        }

        return userService.getByUsername(username)
                .map(user -> {
//                    TODO:
                    user.setUsername(updatedUser.getUsername() == null ? user.getUsername() : updatedUser.getUsername());
                    user.setFirstName(updatedUser.getFirstName() == null ? user.getFirstName() : updatedUser.getFirstName());
                    user.setLastName(updatedUser.getLastName() == null ? user.getLastName() : updatedUser.getLastName());
                    user.setEmail(updatedUser.getEmail() == null ? user.getEmail() : updatedUser.getEmail());
                    user.setCurrentBudget(updatedUser.getCurrentBudget() == null ? user.getCurrentBudget() : updatedUser.getCurrentBudget());
                    user.setExpenseCategories(user.getExpenseCategories() == null ? user.getExpenseCategories() : updatedUser.getExpenseCategories());
                    userService.saveUser(user);
                    return user;
                });
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(String username) {
        if (!(userService.usernameExists(username)))
           throw new NotFoundException("User with username: " + username + " doesn't exist.");

        userService.deleteUser(username);
        return ResponseEntity.ok("User was deleted successfully!");
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
