package com.rigel.ExpenseTracker.controllers;


import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.exception.BadRequestException;
import com.rigel.ExpenseTracker.exception.NotFoundException;
import com.rigel.ExpenseTracker.repositories.ExpenseCategoryRepository;
import com.rigel.ExpenseTracker.repositories.ExpenseTransactionRepository;
import com.rigel.ExpenseTracker.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
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
    public List<ExpenseTransaction> fetchAllExpenseTransactions() {
        return expenseTransactionRepository.findAll();
    }

    @GetMapping("/categories")
    public Set<ExpenseCategory> fetchAllExpenseCategories() {
        return expenseCategoryRepository.findAllCategories();
    }

    @GetMapping("/category/{categoryName}")
    private ExpenseCategory fetchTransactionsByCategory(@PathVariable String categoryName) {
        if(!expenseCategoryRepository.existsExpenseCategoryByCategoryName(categoryName))
            throw new NotFoundException("Category of type " + categoryName + " doesn't exist!");
        return expenseCategoryRepository.findExpenseCategoryByCategoryName(categoryName);
    }

    @GetMapping("/transaction/{id}")
    private ExpenseTransaction fetchTransactionById(@PathVariable Long id) {
        if(!expenseTransactionRepository.existsById(id))
            throw new NotFoundException("Transaction with id " + id + " does not exist.");
        return expenseTransactionRepository.findExpenseTransactionById(id);
    }

    @GetMapping("/transaction/{date}/{userId}")
    private ResponseEntity<?> fetchTransactionsByDate(@PathVariable String date, @PathVariable Long userId) {

        LocalDate day = LocalDate.parse(date);
        User user;

        if (userRepository.existsById(userId))
            user = userRepository.findUserById(userId);
        else
            throw new NotFoundException("User with id:" + userId + " doesn't exist.");

        List<ExpenseTransaction> userTransactions = expenseTransactionRepository.findAllByUser(user);
        List<ExpenseTransaction> response = new ArrayList<>();
        for (ExpenseTransaction transaction : userTransactions)
            if (transaction.getDate().equals(day))
                response.add(transaction);

        if(response.size() == 0)
            throw new NotFoundException("There are no transactions on " + date);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add/category")
    public ResponseEntity<?> addExpenseCategory(String name) {
        if (expenseCategoryRepository.existsExpenseCategoryByCategoryName(name)) {
            throw new BadRequestException("A category with this name already exists!");
        }
        return ResponseEntity.ok(expenseCategoryRepository.save(new ExpenseCategory(name)));
    }

    @PostMapping("/add/transaction")
    public ResponseEntity<?> addExpenseTransaction(Long userId, String date, Double expenseAmount, String expenseCategory, @Nullable String description) {

        Map<String, Object> returnJson = new HashMap<>();

        ExpenseTransaction expenseTransaction;
        LocalDate localDate = LocalDate.parse(date);

        if (!(userRepository.existsById(userId)))
            throw new NotFoundException("User with id:" + userId + " doesn't exist.");

        User user = userRepository.findUserById(userId);
        double userBudget = user.getCurrentBudget() - expenseAmount;
        user.setCurrentBudget(userBudget);
        returnJson.put("userInfo", user);

        if (expenseCategoryRepository.existsExpenseCategoryByCategoryName(expenseCategory)) {
            expenseTransaction = new ExpenseTransaction(localDate, expenseAmount, expenseCategory, expenseCategoryRepository.findExpenseCategoryByCategoryName(expenseCategory), description);
        } else {
            ExpenseCategory nonExistingCategory = new ExpenseCategory();
            nonExistingCategory.setCategoryName(expenseCategory);
            expenseTransaction = new ExpenseTransaction(localDate, expenseAmount, expenseCategory, nonExistingCategory, description);
        }
        expenseTransaction.setUser(user);
        expenseTransactionRepository.save(expenseTransaction);
        returnJson.put("transactionInfo", expenseTransaction);

        return ResponseEntity.ok(returnJson);
    }

    @DeleteMapping("/delete/categories")
    public ResponseEntity<?> deleteAllCategories() {
        if (expenseCategoryRepository.findAll().size() == 0)
            throw new NotFoundException("There are no categories!");

        expenseCategoryRepository.deleteAll();
        return ResponseEntity.ok("All categories have been deleted");
    }

    @DeleteMapping("/delete/category/{id}")
    public ResponseEntity<?> deleteCategoryById(@PathVariable Long id) {
        if (!(expenseCategoryRepository.existsById(id)))
            throw new NotFoundException("Category with id:" + id + " doesn't exist!");

        expenseCategoryRepository.deleteById(id);
        return ResponseEntity.ok("The category has been deleted!");
    }

    @DeleteMapping("/delete/transactions")
    public ResponseEntity<?> deleteAllTransactions() {
        if (expenseTransactionRepository.findAll().size() == 0)
            throw new NotFoundException("There are no transactions!");

        expenseTransactionRepository.deleteAll();
        return ResponseEntity.ok("All transactions have been deleted");
    }

    @DeleteMapping("/delete/transaction/{id}")
    ResponseEntity<?> deleteTransactionById(@PathVariable Long id) {
        if (!(expenseTransactionRepository.existsById(id)))
            throw new NotFoundException("Transaction with id: " + id + " doesn't exist!");

        expenseTransactionRepository.deleteById(id);
        return ResponseEntity.ok("The transaction has been deleted");
    }

}
