package com.rigel.ExpenseTracker.controllers;


import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.exception.BadRequestException;
import com.rigel.ExpenseTracker.exception.NotAllowedException;
import com.rigel.ExpenseTracker.exception.NotFoundException;
import com.rigel.ExpenseTracker.exception.NotValidUrlException;
import com.rigel.ExpenseTracker.repositories.ExpenseCategoryRepository;
import com.rigel.ExpenseTracker.repositories.ExpenseTransactionRepository;
import com.rigel.ExpenseTracker.repositories.UserRepository;
import com.rigel.ExpenseTracker.service.ExpenseCategoryService;
import com.rigel.ExpenseTracker.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rigel.ExpenseTracker.controllers.UserController.createPagination;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ExpenseController {

    private final UserService userService;
    private final ExpenseCategoryService expenseCategoryService;

//    @ApiOperation(value = "Get all expense transactions.", tags = "getTransactions")
    @GetMapping("/expense/transactions")
    public Page<ExpenseTransaction> getAllExpenseTransactions(@Nullable Integer currentPage, @Nullable Integer perPage) {
        Pageable pageable = createPagination(currentPage, perPage, userService.numberOfUsers());
        return expenseCategoryService.getExpenseTransactions(pageable);
    }

    @GetMapping("/expense/categories")
    public Set<ExpenseCategory> getAllExpenseCategories() {
        return expenseCategoryService.getExpenseCategories();
    }

    @GetMapping("/expense/transaction/{id}")
    private ExpenseTransaction getTransactionById(@PathVariable Long id) {
        Optional<ExpenseTransaction> transaction = expenseCategoryService.getTransactionById(id);
        if(transaction.isEmpty())
            throw new NotFoundException("Transaction with id - " + id + " doesn't exist in the DB.");

        return transaction.get();
    }

    @GetMapping("/expense/transactions/user")
    public HashMap<String, Object> getUserTransactions(@RequestParam @Nullable Integer currentPage, @RequestParam @Nullable Integer perPage) {
        Pageable pageable = createPagination(currentPage, perPage, userService.numberOfUsers());
        return expenseCategoryService.getAllUserTransactions(pageable);
    }

    @GetMapping("expense/transaction/{date}")
    private HashMap<String, Object> getTransactionsByDate(@PathVariable String date) {
        return expenseCategoryService.getExpenseTransactionByDate(date);
    }

    @PostMapping("/add/expense/category")
    public ResponseEntity<String> addExpenseCategory(@RequestBody ExpenseCategory category) {
        String name = category.getCategoryName();
        if(name.isEmpty())
            throw new NotValidUrlException("The category must have a name. Please provide it by adding a parameter: name");

        expenseCategoryService.addExpenseCategory(name.toLowerCase());
        return ResponseEntity.ok("Category has been saved successfully!");
    }

    @PostMapping("/add/expense/transaction")
    public ResponseEntity<String> addExpenseTransaction(@RequestParam String date, @RequestParam Double expenseAmount, @RequestParam String categoryName, @RequestParam @Nullable String description) {
        expenseCategoryService.addExpenseTransaction(date, expenseAmount, categoryName, description);
        return ResponseEntity.ok().body("Transaction added successfully");
    }

    @PutMapping("/modify/expense/transaction/{transactionId}")
    public ResponseEntity<?> modifyTransaction(@PathVariable Long transactionId, @RequestBody ExpenseTransaction modifiedTransaction){
        if(!expenseCategoryService.expenseTransactionExists(transactionId))
            throw new NotFoundException("There is no transaction with id: " + transactionId);
        if(modifiedTransaction.getExpenseTransactionId() != null || !(modifiedTransaction.getExpenseTransactionId() == transactionId))
            throw new NotAllowedException("Either provide the same id or don't provide id for the transaction at all.");

        String dbCategoryName = modifiedTransaction.getCategory().toLowerCase();
        Optional<ExpenseCategory> expenseCategory = expenseCategoryService.getOptionalExpenseCategory(dbCategoryName);
        if(expenseCategory.isEmpty()){
            expenseCategoryService.saveExpenseCategoryToDB(new ExpenseCategory(dbCategoryName));
        }

        return expenseCategoryService.getTransactionById(transactionId)
                .map(transaction -> {
                    transaction.setExpenseCategory(modifiedTransaction.getExpenseCategory() == null ? transaction.getExpenseCategory() : modifiedTransaction.getExpenseCategory());
                    transaction.setDate(modifiedTransaction.getDate() == null ? transaction.getDate() : modifiedTransaction.getDate());
                    transaction.setDescription(modifiedTransaction.getDescription() == null ? transaction.getDescription() : modifiedTransaction.getDescription());
                    transaction.setCategory(modifiedTransaction.getCategory() == null ? transaction.getCategory() : modifiedTransaction.getCategory());

                    setBudgetOfUser(transaction, modifiedTransaction.getExpenseAmount());

                    expenseCategoryService.saveExpenseTransactionToDB(transaction);
                    return ResponseEntity.ok().body(transaction);
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/expense/category")
    public ResponseEntity<String> deleteExpenseCategory(String categoryName) {
        expenseCategoryService.deleteExpenseCategory(categoryName);
        return ResponseEntity.ok().body("The category has been deleted!");
    }

    @DeleteMapping("/delete/expense/transactions/user")
    public ResponseEntity<String> deleteAllUserExpenseTransactions() {
        expenseCategoryService.deleteTransactionByUser();
        return ResponseEntity.ok().body("All transactions have been deleted successfully!");
    }

    @DeleteMapping("/delete/expense/transactions/category")
    public ResponseEntity<String> deleteAllUserExpenseTransactionsByCategory(String categoryName) {
        expenseCategoryService.deleteTransactionsByCategory(categoryName);
        return ResponseEntity.ok().body("All transactions in category - " + categoryName + " have been deleted successfully!");
    }

    @DeleteMapping("/delete/expense/transaction/{id}")
    ResponseEntity<String> deleteExpenseTransactionById(@PathVariable Long id) {
        expenseCategoryService.deleteTransactionById(id);
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
    private void deleteTransactionsByCategory(ExpenseCategory category, User user){
        for (ExpenseTransaction transaction : category.getExpenseTransactions()){
            if(transaction.getUser() == user || transaction.getUser().equals(user))
                expenseCategoryService.deleteTransactionById(transaction.getExpenseTransactionId());
        }
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
