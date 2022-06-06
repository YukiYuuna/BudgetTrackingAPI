package com.rigel.ExpenseTracker.controllers;


import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.service.TransactionService;
import com.rigel.ExpenseTracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    public Optional<?> fetchTransactionById(@PathVariable Long id) {
        return service.getTransactionById("expense", id);
    }

    @GetMapping("/expense/transactions/date")
    public HashMap<String, Object> fetchTransactionsByDate(String date, @Nullable Integer currentPage, @Nullable Integer perPage) {
        return service.getTransactionByDate(createPagination(currentPage, perPage, userService.numberOfUsers()), date, "expense");
    }

    @GetMapping("/expense/transactions/year/{year}")
    public HashMap<String, Object> fetchTransactionsByYear(@PathVariable Integer year) {
        return service.getTransactionByYear(year, "expense");
    }

    @GetMapping("/expense/transactions/current/{year}/{month}")
    public HashMap<String, Object> fetchTransactionsByYear(@PathVariable Integer year, @PathVariable Integer month) {
        return service.getTransactionForCurrentMonth(year, month, "expense");
    }

    @GetMapping("/expense/transactions/current/year")
    public HashMap<String, Object> fetchTransactionsByCurrentYear() {
        return service.getTransactionByCurrentYear("expense");
    }

    @GetMapping("/expense/transactions/year/{year}/{month}")
    public HashMap<String, Object> fetchTransactionsByYearMonthAndCategory(@PathVariable Integer year,
                                                           @PathVariable Integer month) {
        return service.getTransactionByYearMonthAndCategory(year, month, "expense");
    }

    @GetMapping("/expense/transactions/category")
    public HashMap<String, Object> fetchTransactionsByCategory(@RequestParam String categoryName, @Nullable Integer currentPage, @Nullable Integer perPage) {
        return service.getTransactionsByCategoryAndUsername(createPagination(currentPage, perPage, userService.numberOfUsers()),"expense", categoryName);
    }

    @PostMapping(value = "/add/expense/category", consumes = "application/json")
    public ResponseEntity<String> addExpenseCategory(@RequestBody ExpenseCategory category) {
        if(category.getColor() != null){
            service.addCategoryWithColor(category.getCategoryName(), "expense", category.getColor());
        } else {
            service.addCategory(category.getCategoryName(), "expense");
        }
        return ResponseEntity.ok("Expense category has been saved successfully!");
    }

    @PostMapping(value = "/add/expense/transaction", consumes = "application/json")
    public ResponseEntity<String> addExpenseTransaction(@RequestBody ExpenseTransaction transaction) {
        service.addTransaction("expense",transaction.getDate().toString(), transaction.getExpenseAmount(), transaction.getCategoryName().toLowerCase(), transaction.getDescription());
        return ResponseEntity.ok().body("Transaction added successfully!");
    }

    @PutMapping("/modify/expense/category/{categoryId}")
    public ResponseEntity<?> modifyExpenseCategory(@PathVariable Long categoryId, @RequestBody ExpenseCategory modifiedCategory) {
        if(!service.categoryExists("expense", categoryId)) {
            throw new ResponseStatusException(NOT_FOUND, "Expense category with this name doesn't exist in the DB.");
        }

        if(modifiedCategory.getExpenseCategoryId() != null) {
            throw new ResponseStatusException(NOT_ACCEPTABLE, "Don't provide an id for the new category, because you cannot modify it.");
        }

        Optional<ExpenseCategory> category = ((Optional<ExpenseCategory>)service.getCategory("expense", categoryId));

//        Provide the user with button which, he/she can use to delete all transaction correlated with this category!
        if(category.get().getExpenseTransactions() != null && category.get().getExpenseTransactions().size() > 0){
            throw new ResponseStatusException(NOT_ACCEPTABLE, "There are attached transactions to category: " + categoryId
                    + ", please either delete those transactions or ADD the category as a new one!");
        }

        return category.map(c -> {
                    modifiedCategory.setCategoryName(modifiedCategory.getCategoryName() == null ? c.getCategoryName() : modifiedCategory.getCategoryName().toLowerCase());
                    modifiedCategory.setUser(c.getUser());
                    modifiedCategory.setColor(c.getColor() == null ? c.getColor() : modifiedCategory.getColor());

                    service.deleteCategory(categoryId, "expense");
                    service.saveCategoryToDB(Optional.of(modifiedCategory), "expense");

//                    new category changes its ID automatically. Old ID frees up.
                    return ResponseEntity.ok().body(modifiedCategory);
                }).orElse(ResponseEntity.notFound().build());
    }

//    Can be cleaner.
    @PutMapping("/modify/expense/transaction/{transactionId}")
    public ResponseEntity<?> modifyExpenseTransaction(@PathVariable Long transactionId, @RequestBody ExpenseTransaction modifiedTransaction){
        Optional<ExpenseTransaction> transaction = ((Optional<ExpenseTransaction>)service.getTransactionById("expense", transactionId));

        if(transaction.isEmpty())
            throw new ResponseStatusException(NOT_FOUND,"There is no transaction with id: " + transactionId);

        if(modifiedTransaction.getExpenseTransactionId() != null)
            throw new ResponseStatusException(NOT_ACCEPTABLE, "Don't provide an id for the new transaction, because you cannot modify it.");

        if (modifiedTransaction.getCategoryName() != null) {
            if (service.categoryExistsByName("expense", modifiedTransaction.getCategoryName())) {
                modifiedTransaction.setExpenseCategory((ExpenseCategory) service
                        .getCategoryByName("expense", modifiedTransaction.getCategoryName()).get());
            } else {
                Optional<ExpenseCategory> newCategory = Optional.of(
                        new ExpenseCategory(modifiedTransaction.getCategoryName(), userService.getUser()));
                service.saveCategoryToDB(newCategory, "expense");
                modifiedTransaction.setExpenseCategory(newCategory.get());
            }
        }

        return transaction.map(t -> {
                    modifiedTransaction.setCategoryName(modifiedTransaction.getCategoryName() == null ? t.getCategoryName().toLowerCase() : modifiedTransaction.getCategoryName().toLowerCase());
                    modifiedTransaction.setDate(modifiedTransaction.getDate() == null ? t.getDate() : modifiedTransaction.getDate());
                    modifiedTransaction.setDescription(modifiedTransaction.getDescription() == null ? t.getDescription() : modifiedTransaction.getDescription());
                    modifiedTransaction.setUser(t.getUser());

                    setBudgetOfUser(t,modifiedTransaction);

                    service.deleteTransactionById("expense", transactionId);
                    service.saveTransactionToDB(Optional.of(modifiedTransaction),"expense");
                    return ResponseEntity.ok().body(modifiedTransaction);
                }).orElse(ResponseEntity.notFound().build());
    }

    /* Ask the user if he wants to delete the category for sure, before calling this method,
    * because if he deletes the category, all transactions made with this category will be deleted too.
    */
    @DeleteMapping("/delete/expense/category/{categoryId}")
    public ResponseEntity<String> deleteExpenseCategory(@PathVariable Long categoryId) {
        service.deleteCategory(categoryId, "expense");
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
        service.deleteTransactionById("expense", id);
        return ResponseEntity.ok().body("The transaction has been deleted successfully!");
    }

    private void setBudgetOfUser(ExpenseTransaction transaction, ExpenseTransaction modTransaction){
        if(modTransaction.getExpenseAmount() != null) {
            Double change = modTransaction.getExpenseAmount() - transaction.getExpenseAmount();
            User user = transaction.getUser();
            user.setCurrentBudget(user.getCurrentBudget() - change);
        } else{
            modTransaction.setExpenseAmount(transaction.getExpenseAmount());
        }
    }
}
