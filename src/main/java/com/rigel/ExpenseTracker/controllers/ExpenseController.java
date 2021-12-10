package com.rigel.ExpenseTracker.controllers;


import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.exception.BadRequestException;
import com.rigel.ExpenseTracker.exception.NotFoundException;
import com.rigel.ExpenseTracker.exception.NotValidUrlException;
import com.rigel.ExpenseTracker.repositories.ExpenseCategoryRepository;
import com.rigel.ExpenseTracker.repositories.ExpenseTransactionRepository;
import com.rigel.ExpenseTracker.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rigel.ExpenseTracker.controllers.UserController.createPagination;

@RestController
@RequestMapping("/api/expense")
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
    public ResponseEntity<?> filterTransactions(Long userId, @Nullable Integer currentPage, @Nullable Integer perPage) {

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
    public ResponseEntity<?> addExpenseCategory(String name) {
        if(name == null || name.isEmpty())
            throw new NotValidUrlException("The category must have a name. Please provide it by adding a parameter: name");

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

        User user = userRepository.fetchUserById(userId);
        double userBudget = user.getCurrentBudget() - expenseAmount;
        user.setCurrentBudget(userBudget);
        returnJson.put("userInfo", user);

        if (expenseCategoryRepository.existsExpenseCategoryByCategoryName(expenseCategory)) {
            expenseTransaction = new ExpenseTransaction(localDate,
                    expenseAmount,
                    expenseCategory,
                    expenseCategoryRepository.findExpenseCategoryByCategoryName(expenseCategory),
                    description);
        } else {
            ExpenseCategory nonExistingCategory = new ExpenseCategory(expenseCategory);
            expenseTransaction = new ExpenseTransaction(localDate, expenseAmount, expenseCategory, nonExistingCategory, description);
        }
        expenseTransaction.setUser(user);
        expenseTransactionRepository.save(expenseTransaction);
        returnJson.put("transactionInfo", expenseTransaction);

        return ResponseEntity.ok(returnJson);
    }

    @PutMapping("/transaction/modify/{id}")
    public ResponseEntity<?> modifyTransaction(@RequestBody ExpenseTransaction expenseTransaction, @PathVariable Long id){
        if(!expenseTransactionRepository.existsById(id)){
            throw new NotFoundException("There is no transaction with id: " + id);
        }

        if(!expenseCategoryRepository.existsExpenseCategoryByCategoryName(expenseTransaction.getCategory())){
            expenseCategoryRepository.save(new ExpenseCategory(expenseTransaction.getCategory()));
        }

        return expenseTransactionRepository.findById(id  )
                .map(transaction -> {
                    transaction.setExpenseCategory(expenseTransaction.getExpenseCategory());
                    transaction.setExpenseAmount(expenseTransaction.getExpenseAmount());
                    transaction.setDate(expenseTransaction.getDate());
                    transaction.setDescription(expenseTransaction.getDescription());
                    transaction.setCategory(expenseTransaction.getCategory());
                    return ResponseEntity.ok(expenseTransactionRepository.save(transaction));
                })
                .orElseGet(() -> {
                    return ResponseEntity.ok(expenseTransactionRepository.save(expenseTransaction));
                });
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
