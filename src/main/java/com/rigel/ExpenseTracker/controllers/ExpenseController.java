package com.rigel.ExpenseTracker.controllers;


import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.exceptions.MyResourceNotFoundException;
import com.rigel.ExpenseTracker.repositories.ExpenseCategoryRepository;
import com.rigel.ExpenseTracker.repositories.ExpenseTransactionRepository;
import com.rigel.ExpenseTracker.repositories.UserRepository;
import org.apache.tomcat.jni.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/expense")
@CrossOrigin(origins = "*")
public class ExpenseController {

    private final UserRepository userRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ExpenseTransactionRepository expenseTransactionRepository;

    public ExpenseController(UserRepository userRepository, ExpenseCategoryRepository expenseCategoryRepository, ExpenseTransactionRepository expenseTransactionRepository) {
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.expenseTransactionRepository = expenseTransactionRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/transactions")
    public List<ExpenseTransaction> fetchAllExpenseTransactions(){
        return expenseTransactionRepository.findAll();
    }

    @GetMapping("/categories")
    public Set<ExpenseCategory> fetchAllExpenseCategories(){
        return expenseCategoryRepository.findAllCategories();
    }

    @GetMapping("/transactions/category")
    private List<ExpenseTransaction> fetchAllTransactionsByCategoryName(String categoryName){
        return expenseTransactionRepository.findExpenseTransactionByExpenseCategory_CategoryName(categoryName);
    }

    @GetMapping("/transaction/{id}")
    private ExpenseTransaction fetchTransactionById(@PathVariable Long id){
        return expenseTransactionRepository.findExpenseTransactionById(id);
    }

    @GetMapping("/transaction/{date}/{userId}")
    private ResponseEntity<?> fetchTransactionsByDate(@PathVariable String date, @PathVariable Long userId)
            throws ResponseStatusException {
        List<ExpenseTransaction> response = new ArrayList<>();
        try {
            LocalDate day = LocalDate.parse(date);
            User user;
            if (userRepository.existsById(userId)) {
                user = userRepository.findUserById(userId);
            } else {
                throw new MyResourceNotFoundException("User not found");
            }
            List<ExpenseTransaction> userTransactions = expenseTransactionRepository.findAllByUser(user);

            for (ExpenseTransaction transaction : userTransactions)
                if (transaction.getDate().equals(day))
                    response.add(transaction);
        } catch (MyResourceNotFoundException exc) {
            return ResponseEntity.ok(exc.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add/category")
    public ResponseEntity<?> addExpenseCategory(String name){
        if(expenseCategoryRepository.existsExpenseCategoryByCategoryName(name)){
            return ResponseEntity.ok("The category already exists!!");
        }
        return ResponseEntity.ok(expenseCategoryRepository.save(new ExpenseCategory(name)));
    }

    @PostMapping("/add/transaction")
    public ResponseEntity<?> addExpenseTransaction(Long userId, String date, Double expenseAmount, String expenseCategory, @Nullable String description){

        Map<String, Object> returnJson = new HashMap<>();

        ExpenseTransaction expenseTransaction;
        LocalDate localDate = LocalDate.parse(date);

        if(!(userRepository.existsById(userId)))
            return ResponseEntity.ok("User not found! Transaction hasn't been made!");

        User user = userRepository.findUserById(userId);
        double userBudget = user.getCurrentBudget() - expenseAmount;
        user.setCurrentBudget(userBudget);
        returnJson.put("userInfo", user);

        if(expenseCategoryRepository.existsExpenseCategoryByCategoryName(expenseCategory)) {
            expenseTransaction = new ExpenseTransaction(localDate, expenseAmount, expenseCategory, expenseCategoryRepository.findExpenseCategoryByCategoryName(expenseCategory), description);
        }
        else{
            ExpenseCategory nonExistingCategory = new ExpenseCategory();
            nonExistingCategory.setCategoryName(expenseCategory);

            expenseTransaction = new ExpenseTransaction(localDate,expenseAmount,expenseCategory, nonExistingCategory, description);
        }
        expenseTransaction.setUser(user);
        expenseTransactionRepository.save(expenseTransaction);
        returnJson.put("transactionInfo", expenseTransaction);

        return ResponseEntity.ok(returnJson);
    }

    @DeleteMapping("/delete/categories")
    public ResponseEntity<?> deleteAllCategories(){
        if(expenseCategoryRepository.findAll().size() == 0)
            return  ResponseEntity.ok("There are no declared categories.");

        expenseCategoryRepository.deleteAll();
        return ResponseEntity.ok("All categories have been deleted");
    }

    @DeleteMapping("/delete/category/{id}")
    public ResponseEntity<?> deleteCategoryById(@PathVariable Long id){
        if(!(expenseCategoryRepository.existsById(id)))
            return ResponseEntity.ok("The category doesn't exist!");

        expenseCategoryRepository.deleteById(id);
        return ResponseEntity.ok("The category has been deleted!");
    }

    @DeleteMapping("/delete/transactions")
    public ResponseEntity<?> deleteAllTransactions(){
        expenseTransactionRepository.deleteAll();
        return ResponseEntity.ok("All transactions have been deleted");
    }

    @DeleteMapping("/delete/transaction/{id}")
    ResponseEntity<?> deleteTransactionById(@PathVariable Long id){
        if(!(expenseTransactionRepository.existsById(id)))
            return ResponseEntity.ok("The transaction doesn't exist!");

        expenseTransactionRepository.deleteById(id);
        return ResponseEntity.ok("The transaction has been deleted");
    }

}
