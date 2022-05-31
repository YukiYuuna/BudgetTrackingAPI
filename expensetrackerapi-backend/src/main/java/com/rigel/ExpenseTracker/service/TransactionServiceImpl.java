package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.*;
import com.rigel.ExpenseTracker.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    public TransactionServiceImpl(@Lazy UserRepository userRepo, ExpenseCategoryRepository expenseCategoryRepo,
                                  IncomeCategoryRepository incomeCategoryRepo, ExpenseTransactionRepository expenseTransactionRepo,
                                  IncomeTransactionRepository incomeTransactionRepo) {
        this.userRepo = userRepo;
        this.expenseCategoryRepo = expenseCategoryRepo;
        this.incomeCategoryRepo = incomeCategoryRepo;
        this.expenseTransactionRepo = expenseTransactionRepo;
        this.incomeTransactionRepo = incomeTransactionRepo;
    }

    @Override
    public void saveCategoryToDB(Optional<?> category, String type) {
        if(type.equals("expense"))
            expenseCategoryRepo.save((ExpenseCategory) category.get());
        else
            incomeCategoryRepo.save((IncomeCategory) category.get());
        log.info("Category has been saved successfully!");
    }

    @Override
    public void saveTransactionToDB(Optional<?> transaction, String categoryType) {
        if(categoryType.equals("expense"))
            expenseTransactionRepo.save((ExpenseTransaction) transaction.get());
        else
            incomeTransactionRepo.save((IncomeTransaction) transaction.get());
        log.info("Transaction has been saved successfully!");
    }

    @Override
    public int numberOfTransactionsByCategory(String type, String categoryName) {
        int transactions;
        if(type.equals("expense")){
            transactions = (int) expenseTransactionRepo.findExpenseTransactionsByCategoryName(categoryName.toLowerCase())
                    .stream().filter(t -> t.getUser().getUserId().equals(getUser().getUserId())).count();
        } else{
            transactions = (int) incomeTransactionRepo.findIncomeTransactionsByCategoryName(categoryName.toLowerCase())
                    .stream().filter(t -> t.getUser().getUserId().equals(getUser().getUserId())).count();
        }
        log.info("There are a total of: " + transactions + "for the currently logged-in user.");
        return transactions;
    }

    @Override
    public Optional<?> getCategory(String type, Long categoryId) {
        Optional<?> category;

        if(type.equals("expense"))
            category = expenseCategoryRepo.findExpenseCategoryByExpenseCategoryIdAndUser(categoryId, getUser());
        else
            category = incomeCategoryRepo.findIncomeCategoryByIncomeCategoryIdAndUser(categoryId, getUser());

        if(category.isEmpty()) {
            log.error("Category: " + categoryId + " doesn't exist in the DB!");
            throw new ResponseStatusException(NOT_FOUND, "Category with this id doesn't exist.");
        }
        return category;
    }

    @Override
    public Optional<?> getCategoryByName(String type, String categoryName) {
        Optional<?> category;

        if(type.equals("expense"))
            category = expenseCategoryRepo.findExpenseCategoryByCategoryNameAndUser(categoryName, getUser());
        else
            category = incomeCategoryRepo.findIncomeCategoryByCategoryNameAndUser(categoryName, getUser());

        if(category.isEmpty()) {
            log.error("Category: " + categoryName + " doesn't exist in the DB!");
            throw new ResponseStatusException(NOT_FOUND, "Category with this id doesn't exist.");
        }
        return category;
    }

    @Override
    public HashMap<String, Object> getCategories(String type) throws ResponseStatusException{
        Set<?> categories;
        User user = getUser();

        if(type.equals("expense"))
            categories = expenseCategoryRepo.findExpenseCategoriesByUser(user);
        else
            categories = incomeCategoryRepo.findIncomeCategoriesByUser(user);

        if(categories.size() == 0) {
            log.error("There are no registered categories.");
            throw new ResponseStatusException(NOT_FOUND, "There are no registered categories.");
        }

        HashMap<String, Object> result = new LinkedHashMap<>();
        result.put("username", getUsernameByAuthentication());
        result.put("totalCategories", categories);

        return result;
    }

    @Override
    public Optional<?> getTransactionById(String type, Long transactionId) {
        if(getUsernameByAuthentication().equals("admin")){
            if(type.equals("expense"))
                return expenseTransactionRepo.findById(transactionId);
            else
                return incomeTransactionRepo.findById(transactionId);
        }

        return getUser().getExpenseTransactions().stream()
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

        if(transactions.isEmpty()) {
            log.error("There are no registered transactions in the DB for the currently logged-in user.");
            throw new ResponseStatusException(NOT_FOUND, "No transactions found in the DB");
        }

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
                    .filterTransactionsByDate(pageable, username, dateTime);
        } else{
            transactions = incomeTransactionRepo
                    .filteredTransactionsByDate(pageable, username, dateTime);
        }

        if(transactions.isEmpty()) {
            log.error("There are no registered transactions on this date for the currently logged-in user.");
            throw new ResponseStatusException(NOT_FOUND, "No transactions found on " + date);
        }

        HashMap<String, Object> result = new LinkedHashMap<>();
        result.put("username", username);
        result.put("date", date);
        result.put("totalTransactions", transactions.getTotalElements());
        result.put("totalPages", transactions.getTotalPages());
        result.put("transactions", transactions.getContent());

        return result;
    }

    public HashMap<String, Object> getTransactionByYear(Integer year, String type) {
        String username = getUsernameByAuthentication();
        HashMap<String, Object> result = new LinkedHashMap<>();
        result.put("username", username);
        result.put("year", year);

        if(type.equals("expense")){
            List<ExpenseTransaction> transactions = expenseTransactionRepo
                    .filterTransactionsByYear(username, year);
            result.put("transactions", transactions);
            result.put("totalAmount", transactions.stream()
                    .mapToDouble(ExpenseTransaction::getExpenseAmount).sum());
        } else{
            List<IncomeTransaction> transactions = incomeTransactionRepo
                    .filterTransactionsByYear(username, year);
            result.put("transactions", transactions);
            result.put("totalAmount", transactions.stream()
                    .mapToDouble(IncomeTransaction::getIncomeAmount).sum());
        }

        return result;
    }

    public HashMap<String, Object> getTransactionForCurrentMonth(Integer year, Integer month, String type) {
        String username = getUsernameByAuthentication();
        HashMap<String, Object> result = new LinkedHashMap<>();
        result.put("username", username);

        result.put("year_month", year + "_" + month);

        if(type.equals("expense")){
            List<ExpenseTransaction> transactions = expenseTransactionRepo
                    .filterTransactionsForCurrentMonth(username, year, month);
            result.put("transactions", transactions);
            result.put("totalAmount", transactions.stream()
                    .mapToDouble(ExpenseTransaction::getExpenseAmount).sum());
        } else{
            List<IncomeTransaction> transactions = incomeTransactionRepo
                    .filterTransactionsForCurrentMonth(username, year, month);
            result.put("transactions", transactions);
            result.put("totalAmount", transactions.stream()
                    .mapToDouble(IncomeTransaction::getIncomeAmount).sum());
        }

        return result;
    }

    public HashMap<String, Object> getTransactionByCurrentYear(String type) {
        String username = getUsernameByAuthentication();
        int currentYear = LocalDate.now().getYear();
        HashMap<String, Object> result = new LinkedHashMap<>();
        result.put("username", username);
        result.put("currentYear", currentYear);

        if(type.equals("expense")){
            List<ExpenseTransaction> transactions = expenseTransactionRepo
                    .filterTransactionsByYear(username, currentYear);
            result.put("transactions", transactions);
            result.put("totalAmount", transactions.stream()
                    .mapToDouble(ExpenseTransaction::getExpenseAmount).sum());
        } else{
            List<IncomeTransaction> transactions = incomeTransactionRepo
                    .filterTransactionsByYear(username, currentYear);
            result.put("transactions", transactions);
            result.put("totalAmount", transactions.stream()
                    .mapToDouble(IncomeTransaction::getIncomeAmount).sum());
        }

        return result;
    }

    public HashMap<String, Object> getTransactionByYearMonthAndCategory(Integer year, Integer month, String type) {
        String username = getUsernameByAuthentication();
        HashMap<String, Object> result = new LinkedHashMap<>();
        result.put("username", username);

        result.put("year_month", year + "_" + month);

        HashMap<String, Object> categoriesInfo = new LinkedHashMap<>();
        if(type.equals("expense")){
            Set<ExpenseCategory> categories = expenseCategoryRepo
                    .findExpenseCategoriesByUser(
                            userRepo.findUserByUsername(username).get());

            for(ExpenseCategory category : categories){
                HashMap<String, Object> transactionsByCategory = new LinkedHashMap<>();
                List<ExpenseTransaction> transactions = expenseTransactionRepo
                        .filterTransactionsByCategoryYearAndMonth(username, year, month, category.getCategoryName());
                transactionsByCategory.put("totalAmount", transactions.stream()
                        .mapToDouble(ExpenseTransaction::getExpenseAmount).sum());
                transactionsByCategory.put("transactions", transactions);
                categoriesInfo.put(category.getCategoryName(), transactionsByCategory);
            }
            categoriesInfo.put("totalCategories", categories.size());

        } else {
            Set<IncomeCategory> categories = incomeCategoryRepo
                    .findIncomeCategoriesByUser(
                            userRepo.findUserByUsername(username).get());

            for(IncomeCategory category : categories){
                HashMap<String, Object> transactionsByCategory = new LinkedHashMap<>();
                List<IncomeTransaction> transactions = incomeTransactionRepo
                        .filterTransactionsByCategoryYearAndMonth(username, year, month, category.getCategoryName());
                transactionsByCategory.put("totalAmount", transactions.stream()
                        .mapToDouble(IncomeTransaction::getIncomeAmount).sum());
                transactionsByCategory.put("transactions", transactions);
                categoriesInfo.put(category.getCategoryName(), transactionsByCategory);
            }
            categoriesInfo.put("totalCategories", categories.size());
        }

        result.put("categoriesInfo", categoriesInfo);

        return result;
    }


    @Override
    public HashMap<String, Object> getAllUserTransactions(Pageable pageable, String type) {
        Page<?> transactions;
        List<?> allTransactions = null;
        String username = getUsernameByAuthentication();

        if(getUser().getRoles().stream().anyMatch(role -> role.getRoleName().equals("ROLE_ADMIN"))) {
            if(type.equals("expense")){
                transactions = expenseTransactionRepo.filteredTransactions(pageable);
            }else{
                transactions = incomeTransactionRepo.filteredTransactions(pageable);
            }
        }
        else {
            if(type.equals("expense")){
                transactions = expenseTransactionRepo.filterTransactionsByUsername(pageable, username);
                allTransactions = expenseTransactionRepo.getAllTransactionsByUsername(username);
            }else{
                transactions = incomeTransactionRepo.filterTransactionsByUsername(pageable, username);
                allTransactions = incomeTransactionRepo.getAllTransactionsByUsername(username);
            }
        }

        if(transactions.isEmpty() || allTransactions == null)
            throw new ResponseStatusException(NOT_FOUND, "No transactions found in the DB.");

        HashMap<String, Object> result = new LinkedHashMap<>();
        result.put("username", username);
        result.put("totalTransactions", transactions.getTotalElements());
        result.put("totalPages", transactions.getTotalPages());
        result.put("transactions", allTransactions);

        return result;
    }

    @Override
    public void addCategory(String categoryName, String type) {
        String dbName = categoryName.toLowerCase();
        User user = getUser();
        Optional<?> category;
        if(type.equals("expense")){
            category = expenseCategoryRepo.findExpenseCategoryByCategoryNameAndUser(dbName, getUser());
        }else if(type.equals("income")){
            category = incomeCategoryRepo.findIncomeCategoryByCategoryNameAndUser(dbName, getUser());
        } else{
            throw new ResponseStatusException(BAD_REQUEST, "Please enter a valid category type. Either income/expense.");
        }

        if(category.isPresent())
            throw new ResponseStatusException(BAD_REQUEST, "Category with name: " + categoryName + ", already exists.");

        if(type.equals("expense")) {
            ExpenseCategory expenseCategory = new ExpenseCategory(dbName, user);
            user.addExpenseCategoryToUser(expenseCategory);
            expenseCategoryRepo.save(expenseCategory);
        } else{
            IncomeCategory incomeCategory = new IncomeCategory(categoryName, user);
            user.addIncomeCategoryToUser(incomeCategory);
            incomeCategoryRepo.save(incomeCategory);
        }
        log.info("Category has been added to the user successfully!");
    }

    @Override
    public void addTransaction(String categoryType, String date,
                               Double transactionAmount, String categoryName,
                               String description) {
        LocalDate curDate;
        User user = getUser();
        try{
//            Gets the date of the transaction:
            curDate = LocalDate.parse(date);
            if(categoryType.equals("expense")){
//            Create an expense transaction:
                ExpenseTransaction expenseTransaction = new ExpenseTransaction(curDate, transactionAmount,
                        categoryName, description, user);
//            Finds if the expense category exists (based on name/user) and does operations with it:
                Optional<ExpenseCategory> category = expenseCategoryRepo
                        .findExpenseCategoryByCategoryNameAndUser(categoryName, user);

//            If the category doesn't exist, we create it and persist it to the DB:
                if(category.isEmpty()){
                    ExpenseCategory newCategory = new ExpenseCategory(categoryName, user);
                    expenseCategoryRepo.save(newCategory);
                    expenseTransaction.setExpenseCategory(newCategory);
                } else{
                    expenseTransaction.setExpenseCategory(category.get());
                }
//            We calculate the users budget:
                user.addExpenseAmountToUser(expenseTransaction.getExpenseAmount());
//            Saving to DB:
                expenseTransactionRepo.save(expenseTransaction);

            } else {
                IncomeTransaction incomeTransaction = new IncomeTransaction(curDate, transactionAmount,
                        categoryName, description, user);
                Optional<IncomeCategory> category = incomeCategoryRepo
                        .findIncomeCategoryByCategoryNameAndUser(categoryName, user);

                if(category.isEmpty()){
                    IncomeCategory newCategory = new IncomeCategory(categoryName, user);
                    incomeCategoryRepo.save(newCategory);
                    incomeTransaction.setIncomeCategory(newCategory);
                } else{
                    incomeTransaction.setIncomeCategory(category.get());
                }
                user.addIncomeAmountToUser(incomeTransaction.getIncomeAmount());
                incomeTransactionRepo.save(incomeTransaction);
            }
            log.info("Transaction has been added to the currently logged-in user successfully!");
        } catch (DateTimeException dte){
            log.error("The date is in incorrect format. PLease provide it in the following one: YYYY-MM-DD");
            throw new ResponseStatusException(NOT_ACCEPTABLE, "Please, provide a correct format for the date of the transaction.(YYYY-MM-DD)");
        }
    }

    @Override
    public boolean transactionExists(String type, Long transactionId) {
        if(type.equals("expense"))
            return expenseTransactionRepo.existsExpenseTransactionByUserAndExpenseTransactionId(getUser(), transactionId);

        return incomeTransactionRepo.existsIncomeTransactionByUserAndIncomeTransactionId(getUser(), transactionId);
    }

    @Override
    public boolean categoryExists(String type, Long categoryId) {
        if(type.equals("expense"))
            return expenseCategoryRepo.existsExpenseCategoryByExpenseCategoryIdAndUser(categoryId, getUser());

        return incomeCategoryRepo.existsIncomeCategoryByIncomeCategoryIdAndUser(categoryId, getUser());
    }

    @Override
    public boolean categoryExistsByName(String type, String categoryName) {
        if(type.equals("expense"))
            return expenseCategoryRepo.existsExpenseCategoryByCategoryNameAndUser(categoryName, getUser());

        return incomeCategoryRepo.existsIncomeCategoryByCategoryNameAndUser(categoryName, getUser());
    }

    @Override
    public void deleteAllUserTransactions(String type){
        User user = getUser();
        if(type.equals("expense")){
            if(user.getExpenseTransactions().size() == 0) {
                log.error("There are no expense transactions made by " + user.getUsername());
                throw new ResponseStatusException(NOT_FOUND, "There are no expense transactions made by " + user.getUsername());
            }
            expenseTransactionRepo.deleteExpenseTransactionsByUser(user);
        }else{
            if(user.getIncomeTransactions().size() == 0) {
                log.error("There are no expense transactions made by " + user.getUsername());
                throw new ResponseStatusException(NOT_FOUND, "There are no income transactions made by " + user.getUsername());
            }
            incomeTransactionRepo.deleteIncomeTransactionsByUser(user);
        }
        log.info("Transactions have been deleted successfully!");
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
        log.info("Transaction with id : " + transactionId + ", has been deleted successfully!");
    }

    @Override
    public  void deleteTransactionsByCategory(String type, String categoryName) {
        User user = getUser();
        if(type.equals("expense")){
            Optional<ExpenseCategory> category = expenseCategoryRepo
                    .findExpenseCategoryByCategoryNameAndUser(categoryName.toLowerCase(), user);
            if(category.isEmpty())
                throw new ResponseStatusException(NOT_FOUND, "Category with this name doesn't exist.");

            expenseTransactionRepo.deleteExpenseTransactionsByCategoryNameAndUser(categoryName, user);
        }else{
            Optional<IncomeCategory> category = incomeCategoryRepo
                    .findIncomeCategoryByCategoryNameAndUser(categoryName.toLowerCase(), user);
            if(category.isEmpty())
                throw new ResponseStatusException(NOT_FOUND, "Category with this name doesn't exist.");

            incomeTransactionRepo.deleteIncomeTransactionsByCategoryNameAndUser(categoryName, user);
        }
        log.info("Transaction with category : " + categoryName + ", has been deleted successfully!");
    }

    @Override
    public void deleteCategory(Long categoryId, String type) {
        User user = getUser();
        if(type.equals("expense")){
            expenseCategoryRepo.deleteExpenseCategoryByUserAndExpenseCategoryId(
                    user, categoryId);
        }else{
            incomeCategoryRepo.deleteIncomeCategoryByUserAndIncomeCategoryId(
                    user, categoryId);
        }
        log.info("Category has been deleted successfully!");
    }

    private String getUsernameByAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private Optional<User> getOptionalUser() {
        Optional<User> user = userRepo.findUserByUsername(getUsernameByAuthentication());

        if (user.isPresent()) {
            log.info("User with username: " + user.get().getUsername() + " exists in the DB!");
            return user;
        }
        else {
            log.error("User doesn't exist in the DB!");
            throw new ResponseStatusException(BAD_REQUEST, "Sorry, something went wrong.");
        }
    }

    private User getUser(){
        return getOptionalUser().get();
    }
}
