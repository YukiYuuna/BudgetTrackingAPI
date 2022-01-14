package com.rigel.ExpenseTracker.controllers;


import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.repositories.ExpenseCategoryRepository;
import com.rigel.ExpenseTracker.service.TransactionService;
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
public class ExpenseController extends ControlHelper {

    private final UserService userService;
    private final TransactionService service;

    @Autowired
    public ExpenseController(@Lazy UserService userService, TransactionService service){
        this.userService = userService;
        this.service = service;
    }

    @GetMapping("/expense/transactions")
    public HashMap<String, Object>  fetchAllExpenseTransactions(@Nullable Integer currentPage, @Nullable Integer perPage) {
        return service.getAllUserTransactions(createPagination(currentPage, perPage, userService.numberOfUsers()),"expense");
    }

    @GetMapping("/expense/categories")
    public HashMap<String, Object> fetchAllUserExpenseCategories() {
        return service.getCategories("expense");
    }

    @GetMapping("/expense/transaction/{id}")
    private Optional<?> fetchTransactionById(@PathVariable Long id) {
        return service.getTransactionById("expense", id);
    }

    @GetMapping("/expense/transactions/date")
    private HashMap<String, Object> fetchTransactionsByDate(String date, @Nullable Integer currentPage, @Nullable Integer perPage) {
        return service.getTransactionByDate(createPagination(currentPage, perPage, userService.numberOfUsers()), date, "expense");
    }

    @GetMapping("/expense/transactions/category")
    private HashMap<String, Object> fetchTransactionsByCategory(@Nullable Integer currentPage, @Nullable Integer perPage, String categoryName) {
        return service.getTransactionsByCategoryAndUsername(createPagination(currentPage, perPage, userService.numberOfUsers()),"expense", categoryName);
    }

    @PostMapping("/add/expense/category")
    public ResponseEntity<String> addExpenseCategory(@RequestBody ExpenseCategory category) {
        String name = category.getCategoryName().toLowerCase();
        service.addCategory("expense", name);
        return ResponseEntity.ok("Expense category has been saved successfully!");
    }

    @PostMapping("/add/expense/transaction")
    public ResponseEntity<String> addExpenseTransaction(@RequestParam String date, @RequestParam Double expenseAmount,
                                                        @RequestParam String categoryName, @RequestParam @Nullable String description) {
        service.addTransaction("expense",date, expenseAmount, categoryName.toLowerCase(), description);
        return ResponseEntity.ok().body("Transaction added successfully");
    }

    @PutMapping("/modify/expense/category")
    public ResponseEntity<?> modifyExpenseCategory(String categoryName, @RequestBody ExpenseCategory modifiedCategory) {
        if(!service.categoryExists("expense", categoryName))
            throw new ResponseStatusException(NOT_FOUND, "Expense category with this name doesn't exist in the DB.");

        return service.getCategory("expense",categoryName)
                .map(c -> {
                    ((ExpenseCategory)c).setCategoryName(modifiedCategory.getCategoryName() == null ? ((ExpenseCategory) c).getCategoryName() : modifiedCategory.getCategoryName().toLowerCase());
                    return ResponseEntity.ok().body(c);
                }).orElse(ResponseEntity.notFound().build());
    }

//    Can be cleaner.
    @PutMapping("/modify/expense/transaction/{transactionId}")
    public ResponseEntity<?> modifyExpenseTransaction(@PathVariable Long transactionId, @RequestBody ExpenseTransaction modifiedTransaction){
        if(!service.transactionExists("expense",transactionId))
            throw new ResponseStatusException(NOT_FOUND,"There is no transaction with id: " + transactionId);
        if(modifiedTransaction.getExpenseTransactionId() != null)
            throw new ResponseStatusException(NOT_ACCEPTABLE, "Don't provide an id for the new transaction, because you cannot modify it.");

        if(modifiedTransaction.getCategoryName() != null) {
            if(!service.categoryExists("expense", modifiedTransaction.getCategoryName())) {
                service.saveCategoryToDB("expense", modifiedTransaction.getCategoryName());
            }
            modifiedTransaction.setExpenseCategory((ExpenseCategory)service.getCategory("expense",modifiedTransaction.getCategoryName()).get());
        }

        return service.getTransactionById("expense", transactionId)
                .map(t -> {
                    ((ExpenseTransaction) t).setExpenseCategory(modifiedTransaction.getExpenseCategory());
                    ((ExpenseTransaction) t).setDate(modifiedTransaction.getDate() == null ? ((ExpenseTransaction) t).getDate() : modifiedTransaction.getDate());
                    ((ExpenseTransaction) t).setDescription(modifiedTransaction.getDescription() == null ? ((ExpenseTransaction) t).getDescription() : modifiedTransaction.getDescription());
                    ((ExpenseTransaction) t).setCategoryName(modifiedTransaction.getCategoryName() == null ? ((ExpenseTransaction) t).getCategoryName().toLowerCase() : modifiedTransaction.getCategoryName().toLowerCase());

                    setBudgetOfUser(((ExpenseTransaction) t), modifiedTransaction.getExpenseAmount());

                    service.saveTransactionToDB( ((ExpenseTransaction) t).getDate(),
                            ((ExpenseTransaction) t).getExpenseAmount(), ((ExpenseTransaction) t).getCategoryName(),
                            ((ExpenseTransaction) t).getDescription(),"expense");
                    return ResponseEntity.ok().body(t);
                }).orElse(ResponseEntity.notFound().build());
    }

    /* Ask the user if he wants to delete the category for sure, before calling this method,
    * because if he deletes the category, all transactions made with this category will be deleted too.
    */
    @DeleteMapping("/delete/expense/category")
    public ResponseEntity<String> deleteExpenseCategory(String categoryName) {
        service.deleteCategory(categoryName, "expense");
        return ResponseEntity.ok().body("Expense category has been deleted successfully!");
    }

    /* The difference between this method and the deleteExpenseCategory method is that by calling this one, you will delete all correlated transactions to this category, but
     * you will not delete the category!
     */
    @DeleteMapping("/delete/expense/transactions/category")
    public ResponseEntity<String> deleteAllUserExpenseTransactionsByCategory(String categoryName) {
        service.deleteTransactionsByCategory("expense", categoryName);
        return ResponseEntity.ok().body("All transactions in category - " + categoryName + " have been deleted successfully!");
    }

    @DeleteMapping("/delete/expense/transactions")
    public ResponseEntity<String> deleteAllUserExpenseTransactions() {
        service.deleteAllUserTransactions("expense");
        return ResponseEntity.ok().body("All expense transactions have been deleted successfully!");
    }

    @DeleteMapping("/delete/expense/transaction/{id}")
    ResponseEntity<String> deleteExpenseTransactionById(@PathVariable Long id) {
        service.deleteTransactionById("expense",id);
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
}
