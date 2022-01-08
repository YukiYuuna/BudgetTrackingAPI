package com.rigel.ExpenseTracker.controllers;


import com.rigel.ExpenseTracker.entities.*;
import com.rigel.ExpenseTracker.service.IncomeService;
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

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class IncomeController {

    private final UserService userService;
    private final IncomeService incomeService;

    @Autowired
    public IncomeController(@Lazy UserService userService, IncomeService incomeService){
        this.userService = userService;
        this.incomeService = incomeService;
    }

    @GetMapping("/income/transactions/admin")
    public Page<IncomeTransaction> fetchAllIncomeTransactions(@Nullable Integer currentPage, @Nullable Integer perPage) {
        Pageable pageable = createPagination(currentPage, perPage, userService.numberOfUsers());
        return incomeService.getIncomeTransactions(pageable);
    }

    @GetMapping("/income/categories/user")
    public Set<IncomeCategory> fetchAllUserIncomeCategories() {
        return incomeService.getIncomeCategories();
    }

    @GetMapping("/income/transaction/{id}")
    private IncomeTransaction fetchTransactionById(@PathVariable Long id) {
        Optional<IncomeTransaction> transaction = incomeService.getTransactionById(id);
        if(transaction.isEmpty())
            throw new ResponseStatusException(NOT_FOUND, "Transaction with id - " + id + " doesn't exist in the DB.");

        return transaction.get();
    }

    @GetMapping("/income/transactions/user")
    public HashMap<String, Object> fetchUserTransactions(@RequestParam @Nullable Integer currentPage, @RequestParam @Nullable Integer perPage) {
        Pageable pageable = createPagination(currentPage, perPage, userService.numberOfUsers());
        return incomeService.getAllUserTransactions(pageable);
    }

    @GetMapping("/income/transactions/date")
    private HashMap<String, Object> fetchTransactionsByDate(String date) {
        return incomeService.getIncomeTransactionByDate(date);
    }

    @GetMapping("/income/transactions/category")
    private Page<IncomeTransaction> fetchTransactionsByCategory(@RequestParam @Nullable Integer currentPage, @RequestParam @Nullable Integer perPage, String categoryName) {
        Pageable pageable = createPagination(currentPage, perPage, incomeService.numberOfTransactionsByCategory(categoryName));
        return incomeService.getTransactionsByCategoryAndUsername(pageable, categoryName);
    }

    @PostMapping("/add/income/category")
    public ResponseEntity<String> addIncomeCategory(@RequestBody IncomeCategory category) {
        String name = category.getCategoryName();
        if(name.isEmpty())
            throw new ResponseStatusException(NOT_ACCEPTABLE, "The category must have a name. Please provide it by adding a parameter: name");

        incomeService.addIncomeCategory(name.toLowerCase());
        return ResponseEntity.ok("Category has been saved successfully!");
    }

    @PostMapping("/add/income/transaction")
    public ResponseEntity<String> addIncomeTransaction(@RequestParam String date, @RequestParam Double incomeAmount, @RequestParam String categoryName, @RequestParam @Nullable String description) {
        incomeService.addIncomeTransaction(date, incomeAmount, categoryName.toLowerCase(), description);
        return ResponseEntity.ok().body("Transaction added successfully");
    }

    @PutMapping("/modify/income/category")
    public ResponseEntity<?> modifyIncomeCategory(String categoryName, @RequestBody IncomeCategory modifiedCategory) {
        String dbCategoryName = categoryName.toLowerCase();
        if(!incomeService.incomeCategoryExists(dbCategoryName))
            throw new ResponseStatusException(NOT_FOUND, "Category with this name doesn't exist in the DB.");

        return incomeService.getOptionalIncomeCategory(dbCategoryName)
                .map(category -> {
                    category.setCategoryName(modifiedCategory.getCategoryName() == null ? category.getCategoryName() : modifiedCategory.getCategoryName().toLowerCase());
                    return ResponseEntity.ok().body(category);
                }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/modify/income/transaction/{transactionId}")
    public ResponseEntity<?> modifyIncomeTransaction(@PathVariable Long transactionId, @RequestBody IncomeTransaction modifiedTransaction){
        if(!incomeService.incomeTransactionExists(transactionId))
            throw new ResponseStatusException(NOT_FOUND, "There is no transaction with id: " + transactionId);
        if(modifiedTransaction.getIncomeTransactionId() != null)
            throw new ResponseStatusException(NOT_ACCEPTABLE, "Don't provide an id for the new transaction, because you cannot modify it.");

        if(modifiedTransaction.getCategoryName() != null) {
            incomeService.saveIncomeCategoryToDB(modifiedTransaction.getCategoryName());
            modifiedTransaction.setIncomeCategory(incomeService.getIncomeCategory(modifiedTransaction.getCategoryName()));
        }

        return incomeService.getTransactionById(transactionId)
                .map(transaction -> {
                    transaction.setIncomeCategory(modifiedTransaction.getIncomeCategory());
                    transaction.setDate(modifiedTransaction.getDate() == null ? transaction.getDate() : modifiedTransaction.getDate());
                    transaction.setDescription(modifiedTransaction.getDescription() == null ? transaction.getDescription() : modifiedTransaction.getDescription());
                    transaction.setCategoryName(modifiedTransaction.getCategoryName() == null ? transaction.getCategoryName().toLowerCase() : modifiedTransaction.getCategoryName().toLowerCase());

                    setBudgetOfUser(transaction, modifiedTransaction.getIncomeAmount());

                    incomeService.saveIncomeTransactionToDB(transaction);
                    return ResponseEntity.ok().body(transaction);
                }).orElse(ResponseEntity.notFound().build());
    }

    /* Ask the user if he wants to delete the category for sure, before calling this method,
     * because if he deletes the category all transactions, made with this category will be deleted too.
     */
    @DeleteMapping("/delete/income/category/user")
    public ResponseEntity<String> deleteIncomeCategory(String categoryName) {
        incomeService.deleteIncomeCategory(categoryName.toLowerCase());
        return ResponseEntity.ok().body("The category has been deleted!");
    }

    @DeleteMapping("/delete/income/transactions/user")
    public ResponseEntity<String> deleteAllUserIncomeTransactions() {
        incomeService.deleteTransactionByUser();
        return ResponseEntity.ok().body("All transactions have been deleted successfully!");
    }

    /* The difference between this method and the deleteIncomeCategory method is that by calling this one, you will delete all correlated transactions to this category, but
     * you will not delete the category!
     */
    @DeleteMapping("/delete/income/transactions/category")
    public ResponseEntity<String> deleteAllUserIncomeTransactionsByCategory(String categoryName) {
        incomeService.deleteTransactionsByCategory(categoryName.toLowerCase());
        return ResponseEntity.ok().body("All transactions in category - " + categoryName + " have been deleted successfully!");
    }

    @DeleteMapping("/delete/income/transaction/{id}")
    ResponseEntity<String> deleteIncomeTransactionById(@PathVariable Long id) {
        incomeService.deleteTransactionById(id);
        return ResponseEntity.ok().body("The transaction has been deleted successfully!");
    }

    private void setBudgetOfUser(IncomeTransaction transaction, Double modBudget){
        if(modBudget != null) {
            Double change = modBudget - transaction.getIncomeAmount();
            User user = transaction.getUser();
            transaction.setIncomeAmount(modBudget);
            user.setCurrentBudget(user.getCurrentBudget() - change);
            userService.saveUserDataAndFlush(user);
        } else{
            transaction.setIncomeAmount(transaction.getIncomeAmount());
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
