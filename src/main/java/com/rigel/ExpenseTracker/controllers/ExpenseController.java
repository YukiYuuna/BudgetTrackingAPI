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
    public List<ExpenseTransaction> getAllExpenseTransactions() {
        return expenseCategoryService.getExpenseTransactions();
    }

    @GetMapping("/expense/categories")
    public Set<ExpenseCategory> getAllExpenseCategories() {
        return expenseCategoryService.getExpenseCategories();
    }

    @GetMapping("/expense/transaction/{id}")
    private ExpenseTransaction getTransactionById(@PathVariable Long id) {
        Optional<ExpenseTransaction> transaction = expenseCategoryService.getExpenseTransaction(id);
        if(transaction.isEmpty())
            throw new NotFoundException("Transaction with id " + id + " does not exist.");
        return transaction.get();
    }

    @GetMapping("/expense/transactions/category")
    public ResponseEntity<?> filterTransactions(String categoryName, @RequestParam @Nullable Integer currentPage, @RequestParam @Nullable Integer perPage) {

        HashMap<String, Object> result = new LinkedHashMap<>();
        ExpenseCategory expenseCategory = expenseCategoryService.getExpenseCategory(categoryName);

        result.put("category", expenseCategory);
        Pageable pageable = createPagination(currentPage, perPage, expenseCategoryService.getExpenseTransactions().size());
        Page<ExpenseTransaction> expenseTransactions = expenseCategoryService.getFilteredTransactions(pageable, expenseCategory.getCategoryName());

        result.put("totalTransactions", expenseTransactions.getTotalElements());
        result.put("totalPages", expenseTransactions.getTotalPages());
        result.put("transactions", expenseTransactions.getContent());

        return ResponseEntity.ok(result);
    }

    @GetMapping("expense/transaction/{date}/{username}")
    private ResponseEntity<?> getTransactionsByDate(@PathVariable String date, @PathVariable String username) {

        HashMap<String, Object> result = new LinkedHashMap<>();
        LocalDate day = LocalDate.parse(date);
        User user = userService.getUser(username);

        result.put("username", username);
        List<ExpenseTransaction> userTransactions = expenseCategoryService.getTransactionByUser(user);
        List<ExpenseTransaction> expenseTransactions = new ArrayList<>();
        for (ExpenseTransaction transaction : userTransactions)
            if (transaction.getDate().equals(day))
                expenseTransactions.add(transaction);

        if(expenseTransactions.size() == 0)
            throw new NotFoundException("There are no transactions on " + date + ", made by " + username);

        result.put("date", date);
        result.put("totalSpent", expenseTransactions.stream()
                .map(ExpenseTransaction::getExpenseAmount)
                .reduce(0.0, Double::sum));

        result.put("transactions", expenseTransactions);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/add/expense/category")
    public ResponseEntity<ExpenseCategory> addExpenseCategory(@RequestBody ExpenseCategory category) {
        String dbName = category.getCategoryName().toLowerCase();
        if(dbName.isEmpty())
            throw new NotValidUrlException("The category must have a name. Please provide it by adding a parameter: name");

        if (expenseCategoryService.expenseCategoryExists(dbName))
            throw new BadRequestException("A category with this name already exists!");

//        if(category.getId() != null)
//            throw new NotAllowedException("You are not allow to modify the id of the category. It is generated randomly!");

        category.setCategoryName(dbName);
        return ResponseEntity.ok().body(expenseCategoryService.saveExpenseCategory(category));
    }

    @PostMapping("/add/expense/transaction")
    public ResponseEntity<Map<String, Object>> addExpenseTransaction(@RequestParam String username, @RequestParam String date, @RequestParam Double expenseAmount, @RequestParam String categoryName, @RequestParam @Nullable String description) {
        try {
            LocalDate day = LocalDate.parse(date);
            String dbCategoryName = categoryName.toLowerCase();
            Map<String, Object> result = new HashMap<>();

            User user = userService.getUser(username);

            double userBudget = user.getCurrentBudget() - expenseAmount;
            user.setCurrentBudget(userBudget);
            result.put("userInfo", user);

            ExpenseTransaction transaction = new ExpenseTransaction(day, expenseAmount, dbCategoryName, description);
            Optional<ExpenseCategory> category = expenseCategoryService.getOptionalExpenseCategory(dbCategoryName);
            if (category.isPresent()) {
                transaction.setExpenseCategory(category.get());
            } else {
                ExpenseCategory nonExistingCategory = new ExpenseCategory(dbCategoryName);
                transaction.setExpenseCategory(nonExistingCategory);
            }

            transaction.setUser(user);
            expenseCategoryService.saveTransaction(transaction);
            result.put("transactionInfo", transaction);
            return ResponseEntity.ok().body(result);
        } catch(DateTimeException dte){
            throw new BadRequestException("Date has incorrect format. Please provide the date in this format: YYYY-MM-DD");
        }
    }

    @PutMapping("/modify/expense/transaction/{transactionId}")
    public Optional<?> modifyTransaction(@PathVariable Long transactionId, @RequestBody ExpenseTransaction modifiedTransaction){
        if(!expenseCategoryService.expenseTransactionExists(transactionId))
            throw new NotFoundException("There is no transaction with id: " + transactionId);
        if(modifiedTransaction.getId() != null || !(modifiedTransaction.getId() == transactionId))
            throw new NotAllowedException("Either provide the same id or don't provide id for the transaction at all.");

        String dbCategoryName = modifiedTransaction.getCategory().toLowerCase();
        Optional<ExpenseCategory> expenseCategory = expenseCategoryService.getOptionalExpenseCategory(dbCategoryName);
        if(expenseCategory.isEmpty()){
            expenseCategoryService.saveExpenseCategory(new ExpenseCategory(dbCategoryName));
        }

        return expenseCategoryService.getExpenseTransaction(transactionId)
                .map(transaction -> {
                    transaction.setExpenseCategory(modifiedTransaction.getExpenseCategory() == null ? transaction.getExpenseCategory() : modifiedTransaction.getExpenseCategory());
                    transaction.setDate(modifiedTransaction.getDate() == null ? transaction.getDate() : modifiedTransaction.getDate());
                    transaction.setDescription(modifiedTransaction.getDescription() == null ? transaction.getDescription() : modifiedTransaction.getDescription());
                    transaction.setCategory(modifiedTransaction.getCategory() == null ? transaction.getCategory() : modifiedTransaction.getCategory());

                    setBudgetOfUser(transaction, modifiedTransaction.getExpenseAmount());

                    return ResponseEntity.ok().body(expenseCategoryService.saveTransaction(transaction));
                });
    }

    @DeleteMapping("/delete/expense/category")
    public ResponseEntity<String> deleteCategoryById(String username, String categoryName) {
        expenseCategoryService.deleteExpenseCategory(username, categoryName);
        return ResponseEntity.ok().body("The category has been deleted!");
    }

    @DeleteMapping("/delete/expense/category/transactions")
    public ResponseEntity<String> deleteAllUserTransactions(String username, String categoryName) {
        deleteTransactionsByCategory(
                expenseCategoryService.getExpenseCategory(categoryName),
                userService.getUser(username));
        return ResponseEntity.ok().body("All transactions made by user - " + username + ", in category - " + categoryName + ", have been deleted successfully!");
    }

    @DeleteMapping("/delete/expense/transaction/{id}")
    ResponseEntity<String> deleteTransactionById(@PathVariable Long id) {
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
                expenseCategoryService.deleteTransactionById(transaction.getId());
        }
    }
}
