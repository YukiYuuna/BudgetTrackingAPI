package com.rigel.ExpenseTracker.controllers;

import com.rigel.ExpenseTracker.entities.Role;
import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.exception.BadRequestException;
import com.rigel.ExpenseTracker.exception.NotFoundException;
import com.rigel.ExpenseTracker.service.UserService;
import com.sun.istack.Nullable;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
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
    @GetMapping("/user}")
    private ResponseEntity<User> getUserInfo(){
        User user = userService.getUser();
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/users")
    @ApiOperation(value = "Gets all users, registered in the Data Base.",
    notes = "Provide the username, the current page and how many users you want per page, in order to get a response.",
    response = ResponseEntity.class)
    private ResponseEntity<Map<String, Object>> getAllUsers(@Nullable Integer currentPage, @Nullable Integer perPage){

        Pageable pageable = createPagination(currentPage, perPage, userService.numberOfUsers());

        Page<User> users = userService.getUsers(pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("totalUsers", users.getTotalElements());
        response.put("totalPages", users.getTotalPages());
        response.put("users", users.getContent());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/user/save/role")
    public ResponseEntity<Role> saveRole(@RequestBody Role role){
        return ResponseEntity.ok().body(userService.saveRole(role));
    }

    @PostMapping("/user/add/role")
    public ResponseEntity<?> addRoleToUser(@RequestBody String roleName){
        userService.addRoleToUser(roleName);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/user/modify")
    public Optional<User> modifyUserInfo(@RequestBody User updatedUser) {
        if (!userService.usernameExists()) {
            throw new NotFoundException("User with this username doesn't exists.");
        }

        return userService.getOptionalUser()
                .map(user -> {
//                    TODO:
                    user.setUsername(updatedUser.getUsername() == null ? user.getUsername() : updatedUser.getUsername());
                    user.setPassword(user.getPassword());
                    user.setFirstName(updatedUser.getFirstName() == null ? user.getFirstName() : updatedUser.getFirstName());
                    user.setLastName(updatedUser.getLastName() == null ? user.getLastName() : updatedUser.getLastName());
                    user.setEmail(updatedUser.getEmail() == null ? user.getEmail() : updatedUser.getEmail());
                    user.setCurrentBudget(updatedUser.getCurrentBudget() == null ? user.getCurrentBudget() : updatedUser.getCurrentBudget());
                    user.setExpenseCategories(user.getExpenseCategories() == null ? user.getExpenseCategories() : updatedUser.getExpenseCategories());
                    userService.saveUser(user);
                    return user;
                });
    }

    @DeleteMapping("/user/delete")
    public ResponseEntity<?> deleteUser() {
        userService.deleteUser();
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
