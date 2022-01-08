package com.rigel.ExpenseTracker.controllers;


import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.service.ExpenseService;
import com.rigel.ExpenseTracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ExpenseController {

    private final UserService userService;
    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(@Lazy UserService userService, ExpenseService expenseCategoryService){
        this.userService = userService;
        this.expenseService = expenseCategoryService;
    }

    @GetMapping("/expense/transactions/admin")
    public Page<ExpenseTransaction> fetchAllExpenseTransactions(@Nullable Integer currentPage, @Nullable Integer perPage) {
        Pageable pageable = createPagination(currentPage, perPage, userService.numberOfUsers());
        return expenseService.getExpenseTransactions(pageable);
    }

    @GetMapping("/expense/categories/user")
    public Set<ExpenseCategory> fetchAllUserExpenseCategories() {
        return expenseService.getExpenseCategories();
    }

    @GetMapping("/expense/transaction/{id}")
    private ExpenseTransaction fetchTransactionById(@PathVariable Long id) {
        Optional<ExpenseTransaction> transaction = expenseService.getTransactionById(id);
        if(transaction.isEmpty())
            throw new ResponseStatusException(NOT_FOUND,"Transaction with id - " + id + " doesn't exist in the DB.");

        return transaction.get();
    }

    @GetMapping("/expense/transactions/user")
    public HashMap<String, Object> fetchUserTransactions(@RequestParam @Nullable Integer currentPage, @RequestParam @Nullable Integer perPage) {
        Pageable pageable = createPagination(currentPage, perPage, userService.numberOfUsers());
        return expenseService.getAllUserTransactions(pageable);
    }

    @GetMapping("/expense/transactions/date")
    private HashMap<String, Object> fetchTransactionsByDate(String date) {
        return expenseService.getExpenseTransactionByDate(date);
    }

    @GetMapping("/expense/transactions/category")
    private Page<ExpenseTransaction> fetchTransactionsByCategory(@RequestParam @Nullable Integer currentPage, @RequestParam @Nullable Integer perPage, String categoryName) {
        Pageable pageable = createPagination(currentPage, perPage, expenseService.numberOfTransactionsByCategory(categoryName));
        return expenseService.getTransactionsByCategoryAndUsername(pageable, categoryName);
    }

    @PostMapping("/add/expense/category")
    public ResponseEntity<String> addExpenseCategory(@RequestBody ExpenseCategory category) {
        String name = category.getCategoryName();
        if(name.isEmpty())
            throw new ResponseStatusException(NOT_ACCEPTABLE, "The category must have a name. Please provide it by adding a parameter: name");

        expenseService.addExpenseCategory(name.toLowerCase());
        return ResponseEntity.ok("Category has been saved successfully!");
    }

    @PostMapping("/add/expense/transaction")
    public ResponseEntity<String> addExpenseTransaction(@RequestParam String date, @RequestParam Double expenseAmount, @RequestParam String categoryName, @RequestParam @Nullable String description) {
        expenseService.addExpenseTransaction(date, expenseAmount, categoryName.toLowerCase(), description);
        return ResponseEntity.ok().body("Transaction added successfully");
    }

    @PutMapping("/modify/expense/category")
    public ResponseEntity<?> modifyExpenseCategory(String categoryName, @RequestBody ExpenseCategory modifiedCategory) {
        String dbCategoryName = categoryName.toLowerCase();
        if(!expenseService.expenseCategoryExists(dbCategoryName))
            throw new ResponseStatusException(NOT_FOUND, "Category with this name doesn't exist in the DB.");

        return expenseService.getOptionalExpenseCategory(dbCategoryName)
                .map(category -> {
                    category.setCategoryName(modifiedCategory.getCategoryName() == null ? category.getCategoryName() : modifiedCategory.getCategoryName().toLowerCase());
                    return ResponseEntity.ok().body(category);
                }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/modify/expense/transaction/{transactionId}")
    public ResponseEntity<?> modifyExpenseTransaction(@PathVariable Long transactionId, @RequestBody ExpenseTransaction modifiedTransaction){
        if(!expenseService.expenseTransactionExists(transactionId))
            throw new ResponseStatusException(NOT_FOUND,"There is no transaction with id: " + transactionId);
        if(modifiedTransaction.getExpenseTransactionId() != null)
            throw new ResponseStatusException(NOT_ACCEPTABLE, "Don't provide an id for the new transaction, because you cannot modify it.");

        if(modifiedTransaction.getCategoryName() != null) {
            expenseService.saveExpenseCategoryToDB(modifiedTransaction.getCategoryName());
            modifiedTransaction.setExpenseCategory(expenseService.getExpenseCategory(modifiedTransaction.getCategoryName()));
        }

        return expenseService.getTransactionById(transactionId)
                .map(transaction -> {
                    transaction.setExpenseCategory(modifiedTransaction.getExpenseCategory());
                    transaction.setDate(modifiedTransaction.getDate() == null ? transaction.getDate() : modifiedTransaction.getDate());
                    transaction.setDescription(modifiedTransaction.getDescription() == null ? transaction.getDescription() : modifiedTransaction.getDescription());
                    transaction.setCategoryName(modifiedTransaction.getCategoryName() == null ? transaction.getCategoryName().toLowerCase() : modifiedTransaction.getCategoryName().toLowerCase());

                    setBudgetOfUser(transaction, modifiedTransaction.getExpenseAmount());

                    expenseService.saveExpenseTransactionToDB(transaction);
                    return ResponseEntity.ok().body(transaction);
                }).orElse(ResponseEntity.notFound().build());
    }

    /* Ask the user if he wants to delete the category for sure, before calling this method,
    * because if he deletes the category all transactions, made with this category will be deleted too.
    */
    @DeleteMapping("/delete/expense/category/user")
    public ResponseEntity<String> deleteExpenseCategory(String categoryName) {
        expenseService.deleteExpenseCategory(categoryName.toLowerCase());
        return ResponseEntity.ok().body("The category has been deleted!");
    }

    @DeleteMapping("/delete/expense/transactions/user")
    public ResponseEntity<String> deleteAllUserExpenseTransactions() {
        expenseService.deleteTransactionByUser();
        return ResponseEntity.ok().body("All transactions have been deleted successfully!");
    }

    /* The difference between this method and the deleteExpenseCategory method is that by calling this one, you will delete all correlated transactions to this category, but
    * you will not delete the category!
    */
    @DeleteMapping("/delete/expense/transactions/category")
    public ResponseEntity<String> deleteAllUserExpenseTransactionsByCategory(String categoryName) {
        expenseService.deleteTransactionsByCategory(categoryName.toLowerCase());
        return ResponseEntity.ok().body("All transactions in category - " + categoryName + " have been deleted successfully!");
    }

    @DeleteMapping("/delete/expense/transaction/{id}")
    ResponseEntity<String> deleteExpenseTransactionById(@PathVariable Long id) {
        expenseService.deleteTransactionById(id);
        return ResponseEntity.ok().body("The transaction has been deleted successfully!");
    }

    private void setBudgetOfUser(ExpenseTransaction transaction, Double modBudget){
        if(modBudget != null) {
            Double change = modBudget - transaction.getExpenseAmount();
            User user = transaction.getUser();
            transaction.setExpenseAmount(modBudget);
            user.setCurrentBudget(user.getCurrentBudget() - change);
            userService.saveUserDataAndFlush(user);
        } else{
            transaction.setExpenseAmount(transaction.getExpenseAmount());
        }
    }

    static Pageable createPagination(Integer currentPage, Integer perPage, int size) {
        Pageable pageable;
        if((currentPage != null && perPage != null) && (currentPage > 0 && perPage > 0)){
            pageable = PageRequest.of(currentPage - 1, perPage);
        } else if (currentPage == null && perPage == null){
            pageable = PageRequest.of(0, size);
        } else {
            throw new ResponseStatusException(BAD_REQUEST,"The value of currentPage and/or perPage parameters cannot be under or equal to 0.");
        }
        return pageable;
    }

}
