package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.IAuthenticationFacade;
import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.exception.BadRequestException;
import com.rigel.ExpenseTracker.exception.NotFoundException;
import com.rigel.ExpenseTracker.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService{

    private final UserRepository userRepo;
    private final ExpenseCategoryRepository expenseCategoryRepo;
    private final ExpenseTransactionRepository expenseTransactionRepo;
    private final IncomeCategoryRepository incomeCategoryRepo;
    private final IncomeTransactionRepository incomeTransactionRepo;

    @Autowired
    private IAuthenticationFacade authenticationFacade;


    @Override
    public void saveExpenseCategoryToDB(ExpenseCategory category) {
        expenseCategoryRepo.save(category);
    }

    @Override
    public void saveExpenseTransactionToDB(ExpenseTransaction transaction) {
        expenseTransactionRepo.save(transaction);
    }

    @Override
    public Optional<ExpenseCategory> getOptionalExpenseCategory(String categoryName) {
        Optional<User> user = userExists(getUsernameByAuthentication());
        return user.get()
                .getExpenseCategories().stream()
                .filter(c -> c.getCategoryName().equals(categoryName))
                .findAny();
    }

    @Override
    public ExpenseCategory getExpenseCategory(String categoryName) {
        Optional<ExpenseCategory> category = getOptionalExpenseCategory(categoryName);
        if(category.isEmpty())
            throw new BadRequestException("A category with this name doesn't exist!");

        return category.get();
    }

    @Override
    public Set<ExpenseCategory> getExpenseCategories() {
        Optional<User> user = userExists(getUsernameByAuthentication());
        return user.get().getExpenseCategories();
    }

    @Override
    public HashMap<String, Object> getAllUserTransactions(Pageable pageable) {
        String username = getUsernameByAuthentication();
        HashMap<String, Object> result = new LinkedHashMap<>();
        Page<ExpenseTransaction> transactions = expenseTransactionRepo.filterTransactionsByUsername(pageable, username);

        result.put("username", username);
        result.put("totalTransactions", transactions.getTotalElements());
        result.put("totalPages", transactions.getTotalPages());
        result.put("transactions", transactions.getContent());

        return result;
    }

    @Override
    public Optional<ExpenseTransaction> getTransactionById(Long transactionId){
        Optional<User> user = userExists(getUsernameByAuthentication());
        return user.get()
                .getExpenseTransactions().stream()
                .filter(transaction -> transaction.getExpenseTransactionId().equals(transactionId))
                .findFirst();
    }

    @Override
    public Page<ExpenseTransaction> getTransactionsByCategoryAndUsername(Pageable pageable, String categoryName, String username) {
        return expenseTransactionRepo.filterTransactionsByUsernameAndCategory(pageable, categoryName, username);
    }

    @Override
    public HashMap<String, Object> getExpenseTransactionByDate(String date) {
        try{
            Optional<User> user = userExists(getUsernameByAuthentication());
            HashMap<String, Object> result = new LinkedHashMap<>();

            List<ExpenseTransaction> transactions = user.get()
                    .getExpenseTransactions().stream()
                    .filter(t -> t.getDate().toString().equals(date))
                    .collect(Collectors.toList());

            if(transactions.size() == 0)
                throw new NotFoundException("No transactions have been made on this date - " + date);

            result.put("username", user.get().getUsername());
            result.put("date", date);
            result.put("totalSpent", transactions.stream()
                    .map(ExpenseTransaction::getExpenseAmount)
                    .reduce(0.0, Double::sum));

            result.put("transactions", transactions);
            return result;
        } catch (DateTimeException e){
            throw new DateTimeException("Please, provide a valid date format: YYYY-MM-DD");
        }
    }

    @Override
    public Page<ExpenseTransaction> getExpenseTransactions(Pageable pageable) {
        return expenseTransactionRepo.findAll(pageable);
    }

    @Override
    public void addExpenseCategory(String categoryName) {
        Optional<User> user = userExists(getUsernameByAuthentication());
        Optional<ExpenseCategory> categories =  user.get().getExpenseCategories()
                .stream().filter(category -> category.getCategoryName().equals(categoryName)).findFirst();

        if (categories.isPresent())
            throw new BadRequestException("Category with this name - " + categoryName + " already exists.");

        ExpenseCategory expenseCategory = new ExpenseCategory(categoryName);
        expenseCategory.setUser(user.get());
        user.get().addExpenseCategoryToUser(expenseCategory);
        expenseCategoryRepo.save(expenseCategory);
    }

    @Override
    public void addExpenseTransaction(String date, Double expenseAmount, String categoryName, String description) {
        Optional<User> user = userExists(getUsernameByAuthentication());

        LocalDate curDate;
        try{
//            Gets the date of the transaction:
            curDate = LocalDate.parse(date);

//            Creates a new transaction from tha params provided:
            ExpenseTransaction expenseTransaction = new ExpenseTransaction(curDate, expenseAmount, categoryName, description);

//            Sets the user to the transaction:
            expenseTransaction.setUser(user.get());

//            Finds if the expense category exists and does operations with it:
            Optional<ExpenseCategory> category = expenseCategoryRepo.findExpenseCategoryByCategoryName(categoryName);

            if(category.isEmpty()){
                ExpenseCategory expenseCategory = new ExpenseCategory(categoryName);
                user.get().addExpenseCategoryToUser(expenseCategory);
                expenseCategory.addTransactionToExpenseCategory(expenseTransaction);
            }else{
                category.get().addTransactionToExpenseCategory(expenseTransaction);
            }
            user.get().addExpenseAmountToUser(expenseTransaction);
            expenseTransactionRepo.save(expenseTransaction);
        } catch (DateTimeException dte){
            throw new DateTimeException("Please, provide a correct format for the date of the transaction.(YYYY-MM-DD)");
        }
    }

    @Override
    public boolean expenseTransactionExists(Long transactionId) {
        if(expenseTransactionRepo.existsExpenseTransactionByUserAndExpenseTransactionId(
                userExists(getUsernameByAuthentication()).get(), transactionId))
            return true;
        return false;
    }

    @Override
    public boolean expenseCategoryExists(String categoryName) {
        boolean exists = userExists(getUsernameByAuthentication()).get()
                .getExpenseCategories()
                .stream()
                .anyMatch(c -> c.getCategoryName().equals(categoryName));
        if(exists)
            return true;
        return false;
    }

    @Override
    public void deleteTransactionByUser() {
        Optional<User> user = userExists(getUsernameByAuthentication());
        if(user.get().getExpenseTransactions().size() == 0)
            throw new NotFoundException("There are no transactions made by " + user.get().getUsername());

        expenseTransactionRepo.deleteExpenseTransactionsByUser(user.get());
    }

    @Override
    public void deleteTransactionById(Long transactionId) {
        Optional<User> user = userExists(getUsernameByAuthentication());
        Optional<ExpenseTransaction> expenseTransaction =  user.get()
                .getExpenseTransactions().stream()
                .filter(transaction -> transaction.getExpenseTransactionId().equals(transactionId))
                .findFirst();
        if(expenseTransaction.isEmpty())
            throw new NotFoundException("Transaction with id: " + transactionId + " doesn't exist!");

        expenseTransactionRepo.delete(expenseTransaction.get());
    }

    @Override
    public void deleteExpenseCategory(String categoryName) {
        expenseCategoryRepo.deleteExpenseCategoryByUserAndAndCategoryName(
                userExists(getUsernameByAuthentication()).get(),
                categoryName);
    }

    @Override
    public void deleteTransactionsByCategory(String categoryName) {
        Optional<User> user = userExists(getUsernameByAuthentication());
        Optional<ExpenseCategory> category = user.get().getExpenseCategories()
                .stream().filter(name -> name.getCategoryName().equals(categoryName)).findFirst();
        if(category.isEmpty())
            throw new NotFoundException("Category with this name doesn't exist.");

        expenseTransactionRepo.deleteExpenseTransactionsByExpenseCategoryAndAndUser(category.get(), user.get());
    }

    public String getUsernameByAuthentication(){
        return authenticationFacade.getAuthentication().getName();
    }

    private Optional<User> userExists(String username){
        Optional<User> user = userRepo.findUserByUsername(username);
        if (user.isEmpty())
            throw new NotFoundException("User with username: " + username + " doesn't exist.");
        return user;
    }
}
