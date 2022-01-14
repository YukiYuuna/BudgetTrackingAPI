package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.*;
import com.rigel.ExpenseTracker.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
@Transactional
@Slf4j
public class TransactionServiceImpl implements TransactionService{

    private final UserRepository userRepo;
    private final ExpenseCategoryRepository expenseCategoryRepo;
    private final IncomeCategoryRepository incomeCategoryRepo;
    private final ExpenseTransactionRepository expenseTransactionRepo;
    private final IncomeTransactionRepository incomeTransactionRepo;

    @Autowired
    public TransactionServiceImpl(UserRepository userRepo, ExpenseCategoryRepository expenseCategoryRepo,
                                  IncomeCategoryRepository incomeCategoryRepo, ExpenseTransactionRepository expenseTransactionRepo,
                                  IncomeTransactionRepository incomeTransactionRepo) {
        this.userRepo = userRepo;
        this.expenseCategoryRepo = expenseCategoryRepo;
        this.incomeCategoryRepo = incomeCategoryRepo;
        this.expenseTransactionRepo = expenseTransactionRepo;
        this.incomeTransactionRepo = incomeTransactionRepo;
    }

    @Override
    public void saveCategoryToDB(String categoryName, String type) {
        String dbName = categoryName.toLowerCase();
        if(type.equals("expense")) {
            expenseCategoryRepo.saveAndFlush(
                    new ExpenseCategory(dbName, getUser().get()));
        } else{
            incomeCategoryRepo.saveAndFlush(
                    new IncomeCategory(dbName, getUser().get()));
        }
    }

    @Override
    public  void saveTransactionToDB(LocalDate date, Double expenseAmount,
                                     String categoryName, String description,
                                     String categoryType) {
        categoryName = categoryName.toLowerCase();

        if(categoryType.equals("expense")) {
            expenseTransactionRepo.saveAndFlush(
                    new ExpenseTransaction(date, expenseAmount, categoryName, description, getUser().get()));

        } else{
            incomeTransactionRepo.saveAndFlush(
                    new IncomeTransaction(date, expenseAmount, categoryName, description, getUser().get()));
        }
    }

    @Override
    public int numberOfTransactionsByCategory(String type, String categoryName) {
        User user = getUser().get();
        int transactions;
        if(type.equals("expense")){
            transactions = (int) expenseTransactionRepo.fetchTransactionsByCategory(categoryName.toLowerCase())
                    .stream().filter(t -> t.getUser().getUserId().equals(user.getUserId())).count();
        } else{
            transactions = (int) incomeTransactionRepo.fetchTransactionsByCategory(categoryName.toLowerCase())
                    .stream().filter(t -> t.getUser().getUserId().equals(user.getUserId())).count();
        }
        return transactions;
    }

    @Override
    public Optional<?> getCategory(String type, String categoryName) {
        Optional<?> category;

        if(type.equals("expense"))
            category = expenseCategoryRepo.fetchCategoryByCategoryNameAndUser(categoryName.toLowerCase(), getUser().get());
        else
            category = incomeCategoryRepo.fetchCategoryByCategoryNameAndUser(categoryName.toLowerCase(), getUser().get());

        if(category.isEmpty())
            throw  new ResponseStatusException(NOT_FOUND, "Category with this name doesn't exist.");

        return category;
    }

    @Override
    public HashMap<String, Object> getCategories(String type) {
        Set<?> categories;

        if(type.equals("expense"))
            categories = expenseCategoryRepo.findAllUserCategories(type, getUsernameByAuthentication());
        else
            categories = incomeCategoryRepo.findAllUserCategories(type, getUsernameByAuthentication());

        if(categories.size() == 0)
            throw new ResponseStatusException(NOT_FOUND, "There are no registered categories.");

        HashMap<String, Object> result = new LinkedHashMap<>();
        result.put("username", getUsernameByAuthentication());
        result.put("totalTransactions", categories);

        return result;
    }

    @Override
    public Optional<?> getTransactionById(String type, Long transactionId) {
        Optional<User> user = getUser();
        if(user.get().getUsername().equals("admin")){
            if(type.equals("expense"))
                return expenseTransactionRepo.findById(transactionId);
            else
                return incomeTransactionRepo.findById(transactionId);
        }
        return user.get()
                .getExpenseTransactions().stream()
                .filter(transaction -> transaction.getExpenseTransactionId().equals(transactionId))
                .findFirst();
    }

    @Override
    public HashMap<String, Object> getTransactionsByCategoryAndUsername(Pageable pageable, String type, String categoryName) {
        categoryName = categoryName.toLowerCase();
        String username = getUsernameByAuthentication();
        Page<?> transactions;

        if(type.equals("expense")){
            transactions = expenseTransactionRepo.filterTransactionsByUsernameAndCategory(pageable,
                    username, categoryName);
        } else{
            transactions = incomeTransactionRepo.filterTransactionsByUsernameAndCategory(pageable,
                    username, categoryName);
        }

        if(transactions.isEmpty())
            throw new ResponseStatusException(NOT_FOUND, "No transactions found in the DB");

        HashMap<String, Object> result = new LinkedHashMap<>();
        result.put("username", username);
        result.put("category", categoryName);
        result.put("totalTransactions", transactions.getTotalElements());
        result.put("totalPages", transactions.getTotalPages());
        result.put("transactions", transactions.getContent());

        return result;
    }

    @Override
    public HashMap<String, Object> getTransactionByDate(Pageable pageable, String date, String type) {
        LocalDate dateTime = LocalDate.parse(date);
        String username = getUsernameByAuthentication();
        Page<?> transactions;

        if(type.equals("expense")){
            transactions = expenseTransactionRepo
                    .filteredTransactionsByDate(pageable, username, dateTime);
        } else{
            transactions = incomeTransactionRepo
                    .filteredTransactionsByDate(pageable, username, dateTime);
        }

        if(transactions.isEmpty())
            throw new ResponseStatusException(NOT_FOUND, "No transactions found on " + date);

        HashMap<String, Object> result = new LinkedHashMap<>();
        result.put("username", username);
        result.put("date", date);
        result.put("totalTransactions", transactions.getTotalElements());
        result.put("totalPages", transactions.getTotalPages());
        result.put("transactions", transactions.getContent());

        return result;
    }

    @Override
    public HashMap<String, Object> getAllUserTransactions(Pageable pageable, String type) {
        String username = getUsernameByAuthentication();
        Page<?> transactions;

        if(getUser().get().getRoles().stream().anyMatch(role -> role.getRoleName().equals("ROLE_ADMIN"))) {
            if(type.equals("expense")){
                transactions = expenseTransactionRepo.filteredTransactions(pageable);
            }else{
                transactions = incomeTransactionRepo.filteredTransactions(pageable);
            }
        }
        else {
            if(type.equals("expense")){
                transactions = expenseTransactionRepo.filterTransactionsByUsername(pageable, username);
            }else{
                transactions = incomeTransactionRepo.filterTransactionsByUsername(pageable, username);
            }
        }

        if(transactions.isEmpty())
            throw new ResponseStatusException(NOT_FOUND, "No transactions found in the DB.");

        HashMap<String, Object> result = new LinkedHashMap<>();
        result.put("username", username);
        result.put("totalTransactions", transactions.getTotalElements());
        result.put("totalPages", transactions.getTotalPages());
        result.put("transactions", transactions.getContent());

        return result;
    }

    @Override
    public void addCategory(String categoryName, String type) {
        Optional<User> user = getUser();
        String dbName = categoryName.toLowerCase();
        Optional<?> category;
        if(type.equals("expense")){
            category = expenseCategoryRepo.fetchCategoryByCategoryNameAndUser(dbName, user.get());
        }else if(type.equals("income")){
            category = incomeCategoryRepo.fetchCategoryByCategoryNameAndUser(dbName, user.get());
        } else{
            throw new ResponseStatusException(BAD_REQUEST, "Please enter a valid category type. Either income/expense.");
        }

        if(category.isPresent())
            throw new ResponseStatusException(BAD_REQUEST, "Category with name: " + categoryName + ", already exists.");

        if(type.equals("expense")) {
            ExpenseCategory expenseCategory = new ExpenseCategory(dbName, user.get());
            user.get().addExpenseCategoryToUser(expenseCategory);
            expenseCategoryRepo.save(expenseCategory);
        } else{
            IncomeCategory incomeCategory = new IncomeCategory(categoryName, user.get());
            user.get().addIncomeCategoryToUser(incomeCategory);
            incomeCategoryRepo.save(incomeCategory);
        }
    }

    @Override
    public void addTransaction(String categoryType, String date,
                               Double transactionAmount, String categoryName,
                               String description) {
        Optional<User> user = getUser();
        LocalDate curDate;
        try{
//            Gets the date of the transaction:
            curDate = LocalDate.parse(date);

            if(categoryType.equals("expense")){
//            Create an expense transaction:
                ExpenseTransaction expenseTransaction = new ExpenseTransaction(curDate, transactionAmount,
                        categoryName, description, user.get());
//            Finds if the expense category exists (based on name/user) and does operations with it:
                Optional<ExpenseCategory> category = expenseCategoryRepo.fetchCategoryByCategoryNameAndUser(categoryName, user.get());

//            If the category doesn't exist, we create it and persist it to the DB:
                if(category.isEmpty()){
                    ExpenseCategory newCategory = new ExpenseCategory(categoryName, user.get());
                    expenseCategoryRepo.save(newCategory);
                    expenseTransaction.setExpenseCategory(newCategory);
                } else{
                    expenseTransaction.setExpenseCategory(category.get());
                }
//            We calculate the users budget:
                user.get().addExpenseAmountToUser(expenseTransaction.getExpenseAmount());
//            Saving to repo:
                expenseTransactionRepo.save(expenseTransaction);

            } else {
                IncomeTransaction incomeTransaction = new IncomeTransaction(curDate, transactionAmount,
                        categoryName, description, user.get());
                Optional<IncomeCategory> category = incomeCategoryRepo.fetchCategoryByCategoryNameAndUser(categoryName, user.get());

                if(category.isEmpty()){
                    IncomeCategory newCategory = new IncomeCategory(categoryName, user.get());
                    incomeCategoryRepo.save(newCategory);
                    incomeTransaction.setIncomeCategory(newCategory);
                } else{
                    incomeTransaction.setIncomeCategory(category.get());
                }
                user.get().addIncomeAmountToUser(incomeTransaction.getIncomeAmount());
                incomeTransactionRepo.save(incomeTransaction);
            }
        } catch (DateTimeException dte){
            throw new ResponseStatusException(NOT_ACCEPTABLE, "Please, provide a correct format for the date of the transaction.(YYYY-MM-DD)");
        }
    }

    @Override
    public boolean transactionExists(String type, Long transactionId) {
        User user = getUser().get();
        if(type.equals("expense"))
            return expenseTransactionRepo.existsExpenseTransactionByUserAndExpenseTransactionId(user, transactionId);

        return incomeTransactionRepo.existsIncomeTransactionByUserAndIncomeTransactionId(user, transactionId);
    }

    @Override
    public boolean categoryExists(String type, String categoryName) {
        User user = getUser().get();
        if(type.equals("expense"))
            return expenseCategoryRepo.existsExpenseCategoryByCategoryNameAndUser(categoryName.toLowerCase(), user);

        return incomeCategoryRepo.existsIncomeCategoryByCategoryNameAndUser(categoryName.toLowerCase(), user);
    }

    @Override
    public void deleteAllUserTransactions(String type){
        User user = getUser().get();

        if(type.equals("expense")){
            if(user.getExpenseTransactions().size() == 0)
                throw new ResponseStatusException(NOT_FOUND,"There are no expense transactions made by " + user.getUsername());

            expenseTransactionRepo.deleteExpenseTransactionsByUser(user);
        }else{
            if(user.getIncomeTransactions().size() == 0)
                throw new ResponseStatusException(NOT_FOUND,"There are no income transactions made by " + user.getUsername());

            incomeTransactionRepo.deleteIncomeTransactionsByUser(user);
        }
    }

    @Override
    public void deleteTransactionById(String type, Long transactionId){
        if(type.equals("expense")){
            Optional<ExpenseTransaction> transaction = expenseTransactionRepo.findById(transactionId);
            if(transaction.isEmpty())
                throw new ResponseStatusException(BAD_REQUEST,"Expense transaction with id: " + transactionId + " doesn't exist!");

            expenseTransactionRepo.delete(transaction.get());
        }else{
            Optional<IncomeTransaction> transaction = incomeTransactionRepo.findById(transactionId);
            if(transaction.isEmpty())
                throw new ResponseStatusException(BAD_REQUEST,"Expense transaction with id: " + transactionId + " doesn't exist!");

            incomeTransactionRepo.delete(transaction.get());
        }

    }

    @Override
    public  void deleteTransactionsByCategory(String type, String categoryName) {
        User user = getUser().get();
        if(type.equals("expense")){
            Optional<ExpenseCategory> category = expenseCategoryRepo.fetchCategoryByCategoryNameAndUser
                    (categoryName.toLowerCase(), user);
            if(category.isEmpty())
                throw new ResponseStatusException(NOT_FOUND, "Category with this name doesn't exist.");

            expenseTransactionRepo.deleteExpenseTransactionsByExpenseCategoryAndUser(category.get(), user);
        }else{
            Optional<IncomeCategory> category = incomeCategoryRepo.fetchCategoryByCategoryNameAndUser
                    (categoryName.toLowerCase(), user);
            if(category.isEmpty())
                throw new ResponseStatusException(NOT_FOUND, "Category with this name doesn't exist.");

            incomeTransactionRepo.deleteIncomeTransactionsByIncomeCategoryAndUser(category.get(), user);
        }
    }

    @Override
    public void deleteCategory(String categoryName, String type) {
        User user = getUser().get();
        if(type.equals("expense")){
            expenseCategoryRepo.deleteExpenseCategoryByUserAndCategoryName(
                    user, categoryName.toLowerCase());
        }else{
            incomeCategoryRepo.deleteIncomeCategoryByUserAndCategoryName(
                    user, categoryName.toLowerCase());
        }
    }

    @Override
    public String getUsernameByAuthentication(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @Override
    public Optional<User> getUser() {
        Optional<User> user = userRepo.findUserByUsername(getUsernameByAuthentication());

        if (user.isPresent()) {
            return user;
        }
        else {
            throw new ResponseStatusException(BAD_REQUEST, "Sorry, something went wrong.");
        }
    }
}
