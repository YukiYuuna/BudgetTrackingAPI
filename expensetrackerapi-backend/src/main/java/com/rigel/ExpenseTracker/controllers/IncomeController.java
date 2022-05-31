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

    @GetMapping("/income/transactions/current/{year}/{month}")
    public HashMap<String, Object> fetchTransactionsByYear(@PathVariable Integer year, @PathVariable Integer month) {
        return service.getTransactionForCurrentMonth(year, month, "income");
    }

    @GetMapping("/income/transactions/current/year")
    public HashMap<String, Object> fetchTransactionsByCurrentYear() {
        return service.getTransactionByCurrentYear("income");
    }

    @GetMapping("/income/transactions/year/{year}/{month}")
    public HashMap<String, Object> fetchTransactionsByYearMonthAndCategory(@PathVariable Integer year,
                                                                           @PathVariable Integer month) {
        return service.getTransactionByYearMonthAndCategory(year, month, "income");
    }

    @GetMapping("/income/transactions/category")
    private HashMap<String, Object> fetchTransactionsByCategory(@RequestParam @Nullable Integer currentPage, @RequestParam @Nullable Integer perPage, String categoryName) {
        return service.getTransactionsByCategoryAndUsername(createPagination(currentPage, perPage, userService.numberOfUsers()),"income", categoryName);
    }

    @PostMapping("/add/income/category")
    public ResponseEntity<String> addIncomeCategory(@RequestBody IncomeCategory category) {
        service.addCategory(category.getCategoryName(), "income");
        return ResponseEntity.ok("Income category has been saved successfully!");
    }

    @PostMapping(value = "/add/income/transaction", consumes = "application/json")
    public ResponseEntity<String> addIncomeTransaction(@RequestBody IncomeTransaction transaction) {
        service.addTransaction("income",transaction.getDate().toString(), transaction.getIncomeAmount(), transaction.getCategoryName().toLowerCase(), transaction.getDescription());
        return ResponseEntity.ok().body("Transaction added successfully!");
    }

    @PutMapping("/modify/income/category")
    public ResponseEntity<?> modifyIncomeCategory(Long categoryId, @RequestBody IncomeCategory modifiedCategory) {
        if(!service.categoryExists("income", categoryId)) {
            throw new ResponseStatusException(NOT_FOUND, "Income category with this name doesn't exist in the DB.");
        }

        if(modifiedCategory.getIncomeCategoryId() != null) {
            throw new ResponseStatusException(NOT_ACCEPTABLE, "Don't provide an id for the new category, because you cannot modify it.");
        }

        Optional<IncomeCategory> category = ((Optional<IncomeCategory>)service.getCategory("income", categoryId));

//        Provide the user with button which, he/she can use to delete all transaction correlated with this category!
        if(category.get().getIncomeTransactions() != null && category.get().getIncomeTransactions().size() > 0){
            throw new ResponseStatusException(NOT_ACCEPTABLE, "There are attached transactions to category - " + categoryId
                    + ", please either delete those transactions or ADD the category as a new one!");
        }

        return category.map(c -> {
            modifiedCategory.setCategoryName(modifiedCategory.getCategoryName() == null ? c.getCategoryName() : modifiedCategory.getCategoryName().toLowerCase());
            modifiedCategory.setUser(c.getUser());

            service.deleteCategory(categoryId, "income");
            service.saveCategoryToDB(Optional.of(modifiedCategory), "income");

//                    new category changes its ID automatically. Old ID frees up.
            return ResponseEntity.ok().body(modifiedCategory);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/modify/income/transaction/{transactionId}")
    public ResponseEntity<?> modifyIncomeTransaction(@PathVariable Long transactionId, @RequestBody IncomeTransaction modifiedTransaction){
        Optional<IncomeTransaction> transaction = ((Optional<IncomeTransaction>)service.getTransactionById("income", transactionId));

        if(transaction.isEmpty())
            throw new ResponseStatusException(NOT_FOUND,"There is no transaction with id: " + transactionId);

        if(modifiedTransaction.getIncomeTransactionId() != null)
            throw new ResponseStatusException(NOT_ACCEPTABLE, "Don't provide an id for the new transaction, because you cannot modify it.");

        if (modifiedTransaction.getCategoryName() != null) {
            if (service.categoryExists("income", modifiedTransaction.getIncomeCategory().getIncomeCategoryId())) {
                modifiedTransaction.setIncomeCategory((IncomeCategory) service
                        .getCategory("income", modifiedTransaction.getIncomeCategory().getIncomeCategoryId()).get());
            } else {
                Optional<IncomeCategory> newCategory = Optional.of(
                        new IncomeCategory(modifiedTransaction.getCategoryName(), userService.getUser()));
                service.saveCategoryToDB(newCategory, "income");
                modifiedTransaction.setIncomeCategory(newCategory.get());
            }
        }

        return transaction.map(t -> {
            modifiedTransaction.setCategoryName(modifiedTransaction.getCategoryName() == null ? t.getCategoryName().toLowerCase() : modifiedTransaction.getCategoryName().toLowerCase());
            modifiedTransaction.setDate(modifiedTransaction.getDate() == null ? t.getDate() : modifiedTransaction.getDate());
            modifiedTransaction.setDescription(modifiedTransaction.getDescription() == null ? t.getDescription() : modifiedTransaction.getDescription());
            modifiedTransaction.setUser(t.getUser());

            setBudgetOfUser(t,modifiedTransaction);

            service.deleteTransactionById("income", transactionId);
            service.saveTransactionToDB(Optional.of(modifiedTransaction),"income");
            return ResponseEntity.ok().body(modifiedTransaction);
        }).orElse(ResponseEntity.notFound().build());
    }

    /* Ask the user if he wants to delete the category for sure, before calling this method,
     * because if he deletes the category all transactions, made with this category will be deleted too.
     */
    @DeleteMapping("/delete/income/category")
    public ResponseEntity<String> deleteIncomeCategory(Long categoryId) {
        service.deleteCategory(categoryId, "income");
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
        service.deleteTransactionById("income", id);
        return ResponseEntity.ok().body("The transaction has been deleted successfully!");
    }

    private void setBudgetOfUser(IncomeTransaction transaction, IncomeTransaction modTransaction){
        if(modTransaction.getIncomeAmount() != null) {
            Double change = modTransaction.getIncomeAmount() - transaction.getIncomeAmount();
            User user = transaction.getUser();
            user.setCurrentBudget(user.getCurrentBudget() - change);
        } else{
            modTransaction.setIncomeAmount(transaction.getIncomeAmount());
        }
    }
}
