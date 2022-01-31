package com.rigel.ExpenseTracker.controllers;


import com.rigel.ExpenseTracker.entities.*;
import com.rigel.ExpenseTracker.service.TransactionService;
import com.rigel.ExpenseTracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Optional;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class IncomeController extends ControlHelper {

    private final UserService userService;
    private final TransactionService service;

    @Autowired
    public IncomeController(@Lazy UserService userService, TransactionService service){
        this.userService = userService;
        this.service = service;
    }

    @GetMapping("/income/transactions")
    public HashMap<String, Object> fetchAllIncomeTransactions(@Nullable Integer currentPage, @Nullable Integer perPage) {
        return service.getAllUserTransactions(createPagination(currentPage, perPage, userService.numberOfUsers()),"income");
    }

    @GetMapping("/income/categories")
    public HashMap<String, Object> fetchAllUserIncomeCategories() {
        return service.getCategories("income");
    }

    @GetMapping("/income/transaction/{id}")
    private Optional<?> fetchTransactionById(@PathVariable Long id) {
        return service.getTransactionById("income", id);
    }

    @GetMapping("/income/transactions/date")
    private HashMap<String, Object> fetchTransactionsByDate(String date, @Nullable Integer currentPage, @Nullable Integer perPage) {
        return service.getTransactionByDate(createPagination(currentPage, perPage, userService.numberOfUsers()), date, "income");
    }

    @GetMapping("/income/transactions/category")
    private HashMap<String, Object> fetchTransactionsByCategory(@RequestParam @Nullable Integer currentPage, @RequestParam @Nullable Integer perPage, String categoryName) {
        return service.getTransactionsByCategoryAndUsername(createPagination(currentPage, perPage, userService.numberOfUsers()),"income", categoryName);

    }

    @PostMapping("/add/income/category")
    public ResponseEntity<String> addIncomeCategory(@RequestBody IncomeCategory category) {
        String name = category.getCategoryName().toLowerCase();
        service.addCategory(name, "income");
        return ResponseEntity.ok("Income category has been saved successfully!");
    }

    @PostMapping("/add/income/transaction")
    public ResponseEntity<String> addIncomeTransaction(@RequestParam String date, @RequestParam Double incomeAmount,
                                                       @RequestParam String categoryName, @RequestParam @Nullable String description) {
        service.addTransaction("income",date, incomeAmount, categoryName.toLowerCase(), description);
        return ResponseEntity.ok().body("Transaction added successfully");
    }

    @PutMapping("/modify/income/category")
    public ResponseEntity<?> modifyIncomeCategory(String categoryName, @RequestBody IncomeCategory modifiedCategory) {
        if(!service.categoryExists("income", categoryName))
            throw new ResponseStatusException(NOT_FOUND, "Income category with this name doesn't exist in the DB.");

        return service.getCategory("income",categoryName)
                .map(c -> {
                    ((IncomeCategory)c).setCategoryName(modifiedCategory.getCategoryName() == null ? ((IncomeCategory) c).getCategoryName() : modifiedCategory.getCategoryName().toLowerCase());
                    return ResponseEntity.ok().body(c);
                }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/modify/income/transaction/{transactionId}")
    public ResponseEntity<?> modifyIncomeTransaction(@PathVariable Long transactionId, @RequestBody IncomeTransaction modifiedTransaction){
        if(!service.transactionExists("income",transactionId))
            throw new ResponseStatusException(NOT_FOUND,"There is no transaction with id: " + transactionId);
        if(modifiedTransaction.getIncomeTransactionId() != null)
            throw new ResponseStatusException(NOT_ACCEPTABLE, "Don't provide an id for the new transaction, because you cannot modify it.");

        if(modifiedTransaction.getCategoryName() != null) {
            if(!service.categoryExists("income", modifiedTransaction.getCategoryName())) {
                service.saveCategoryToDB(Optional.of(new IncomeCategory(modifiedTransaction.getCategoryName(),
                        userService.getUser())), "income");
            }
            modifiedTransaction.setIncomeCategory((IncomeCategory) service.getCategory("income",modifiedTransaction.getCategoryName()).get());
        }

        return service.getTransactionById("income", transactionId)
                .map(t -> {
                    ((IncomeTransaction) t).setIncomeCategory(modifiedTransaction.getIncomeCategory());
                    ((IncomeTransaction) t).setDate(modifiedTransaction.getDate() == null ? ((IncomeTransaction) t).getDate() : modifiedTransaction.getDate());
                    ((IncomeTransaction) t).setDescription(modifiedTransaction.getDescription() == null ? ((IncomeTransaction) t).getDescription() : modifiedTransaction.getDescription());
                    ((IncomeTransaction) t).setCategoryName(modifiedTransaction.getCategoryName() == null ? ((IncomeTransaction) t).getCategoryName().toLowerCase() : modifiedTransaction.getCategoryName().toLowerCase());

                    setBudgetOfUser(((IncomeTransaction) t), modifiedTransaction.getIncomeAmount());

                    service.saveTransactionToDB(Optional.of(modifiedTransaction),"expense");
                    return ResponseEntity.ok().body(t);
                }).orElse(ResponseEntity.notFound().build());
    }

    /* Ask the user if he wants to delete the category for sure, before calling this method,
     * because if he deletes the category all transactions, made with this category will be deleted too.
     */
    @DeleteMapping("/delete/income/category")
    public ResponseEntity<String> deleteIncomeCategory(String categoryName) {
        service.deleteCategory(categoryName, "income");
        return ResponseEntity.ok().body("Income category has been deleted successfully!");
    }

    /* The difference between this method and the deleteIncomeCategory method is that by calling this one, you will delete all correlated transactions to this category, but
     * you will not delete the category!
     */
    @DeleteMapping("/delete/income/transactions/category")
    public ResponseEntity<String> deleteAllUserIncomeTransactionsByCategory(String categoryName) {
        service.deleteTransactionsByCategory("income", categoryName);
        return ResponseEntity.ok().body("All transactions in category - " + categoryName + " have been deleted successfully!");
    }

    @DeleteMapping("/delete/income/transactions")
    public ResponseEntity<String> deleteAllUserIncomeTransactions() {
        service.deleteAllUserTransactions("income");
        return ResponseEntity.ok().body("All income transactions have been deleted successfully!");
    }

    @DeleteMapping("/delete/income/transaction/{id}")
    ResponseEntity<String> deleteIncomeTransactionById(@PathVariable Long id) {
        service.deleteTransactionById("income",id);
        return ResponseEntity.ok().body("The transaction has been deleted successfully!");
    }

    private void setBudgetOfUser(IncomeTransaction transaction, Double modBudget){
        if(modBudget != null) {
            Double change = modBudget - transaction.getIncomeAmount();
            User user = transaction.getUser();
            transaction.setIncomeAmount(modBudget);
            user.setCurrentBudget(user.getCurrentBudget() + change);
            userService.saveUserDataAndFlush(user);
        } else{
            transaction.setIncomeAmount(transaction.getIncomeAmount());
        }
    }
}
