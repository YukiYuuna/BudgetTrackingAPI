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
@RequestMapping("/expenses")
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


    @GetMapping("/transaction/{id}")
    private ExpenseTransaction fetchTransactionById(@PathVariable Long id) {
        if(!expenseTransactionRepository.existsById(id))
            throw new NotFoundException("Transaction with id " + id + " does not exist.");
        return expenseTransactionRepository.findExpenseTransactionById(id);
    }

    @GetMapping("/transactions/user")
    public ResponseEntity<?> filterTransactions(Long userId, @RequestParam @Nullable Integer currentPage, @RequestParam @Nullable Integer perPage) {

        HashMap<String, Object> result = new LinkedHashMap<>();
        if (!userRepository.existsById(userId))
            throw new NotFoundException("User with id:" + userId + " doesn't exist.");

        User user = userRepository.fetchUserById(userId);
        String username = Stream.of(user.getFirstName(), user.getLastName())
                .map(Object::toString)
                .collect(Collectors.joining(" "));
        result.put("user", username);

        Pageable pageable = createPagination(currentPage, perPage, expenseTransactionRepository.findAll().size());

        Page<ExpenseTransaction> expenseTransactions = expenseTransactionRepository.filterTransactions(pageable, user.getEmail());
        result.put("totalTransactions", expenseTransactions.getTotalElements());
        result.put("totalPages", expenseTransactions.getTotalPages());
        result.put("transactions", expenseTransactions.getContent());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/transaction/{date}/{userId}")
    private ResponseEntity<?> fetchTransactionsByDate(@PathVariable String date, @PathVariable Long userId) {

        HashMap<String, Object> result = new LinkedHashMap<>();
        LocalDate day = LocalDate.parse(date);
        User user;
        String username;

        if (userRepository.existsById(userId)) {
            user = userRepository.fetchUserById(userId);
            username = Stream.of(user.getFirstName(), user.getLastName())
                    .map(Object::toString)
                    .collect(Collectors.joining(" "));
            result.put("user", username);
        } else{
            throw new NotFoundException("User with id:" + userId + " doesn't exist.");
        }

        List<ExpenseTransaction> userTransactions = expenseTransactionRepository.findAllByUser(user);
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

    @PostMapping("/add/category")
    public ResponseEntity<?> addExpenseCategory(@RequestBody ExpenseCategory category) {
        String finalName = category.getCategoryName().toLowerCase();
        if(finalName.isEmpty())
            throw new NotValidUrlException("The category must have a name. Please provide it by adding a parameter: name");

        if (expenseCategoryRepository.existsByCategoryName(finalName))
            throw new BadRequestException("A category with this name already exists!");

        if(category.getId() != null)
            throw new NotAllowedException("You are not allow to modify the id of the category. It is generated randomly!");

        category.setCategoryName(finalName);
        return ResponseEntity.ok(expenseCategoryRepository.save(category));
    }

    @PostMapping("/add/transaction")
    public ResponseEntity<?> addExpenseTransaction(@RequestParam Long userId, @RequestParam String date, @RequestParam Double expenseAmount, @RequestParam String categoryName, @RequestParam @Nullable String description) {
        try {
            LocalDate day = LocalDate.parse(date);
            String categoryNameToLower = categoryName.toLowerCase();
            Map<String, Object> returnJson = new HashMap<>();

            if (!(userRepository.existsById(userId)))
                throw new NotFoundException("User with id " + userId + " doesn't exist.");

            User user = userRepository.fetchUserById(userId);
            double userBudget = user.getCurrentBudget() - expenseAmount;
            user.setCurrentBudget(userBudget);
            returnJson.put("userInfo", user);

            ExpenseTransaction transaction = new ExpenseTransaction(day, expenseAmount, categoryNameToLower, description);
            Optional<ExpenseCategory> category = expenseCategoryRepository.findExpenseCategoryByCategoryName(categoryNameToLower);
            if (category.isPresent()) {
                transaction.setExpenseCategory(category.get());
            } else {
                ExpenseCategory nonExistingCategory = new ExpenseCategory(categoryNameToLower);
                transaction.setExpenseCategory(nonExistingCategory);
            }

            transaction.setUser(user);
            expenseTransactionRepository.save(transaction);
            returnJson.put("transactionInfo", transaction);

            return ResponseEntity.ok(returnJson);
        } catch(DateTimeException dte){
            throw new BadRequestException("Date has incorrect format. Please provide the date in this format: YYYY-MM-DD");
        }
    }

    @PutMapping("/modify/transaction/{id}")
    public ResponseEntity<?> modifyTransaction(@RequestBody ExpenseTransaction modifiedTransaction, @PathVariable Long id){
        if(!expenseTransactionRepository.existsById(id)){
            throw new NotFoundException("There is no transaction with id: " + id);
        }

        String categoryName = modifiedTransaction.getCategory().toLowerCase();
        Optional<ExpenseCategory> expenseCategory = expenseCategoryRepository
                .findExpenseCategoryByCategoryName(categoryName);
        if(expenseCategory.isEmpty()){
            expenseCategoryRepository.save(new ExpenseCategory(categoryName));
        }

        return expenseTransactionRepository.findById(id)
                .map(transaction -> {
                    transaction.setExpenseCategory(modifiedTransaction.getExpenseCategory() == null ? transaction.getExpenseCategory() : modifiedTransaction.getExpenseCategory());
                    transaction.setDate(modifiedTransaction.getDate() == null ? transaction.getDate() : modifiedTransaction.getDate());
                    transaction.setDescription(modifiedTransaction.getDescription() == null ? transaction.getDescription() : modifiedTransaction.getDescription());
                    transaction.setCategory(modifiedTransaction.getCategory() == null ? transaction.getCategory() : modifiedTransaction.getCategory());

                    setBudgetOfUser(transaction, transaction.getExpenseAmount(), modifiedTransaction.getExpenseAmount());

                    return ResponseEntity.ok(expenseTransactionRepository.save(transaction));
                })
                .orElseThrow(() -> new NotFoundException("There is no transaction with id " + id));
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

    private void setBudgetOfUser(ExpenseTransaction transaction, Double curBudget, Double modBudget){
        if(modBudget != null) {
            Double change = modBudget - curBudget;
            User user = transaction.getUser();
            transaction.setExpenseAmount(modBudget);
            user.setCurrentBudget(user.getCurrentBudget() - change);
            userRepository.saveAndFlush(user);
        } else{
            transaction.setExpenseAmount(curBudget);
        }
    }
}
