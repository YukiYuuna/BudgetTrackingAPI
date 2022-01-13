package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.*;
import com.rigel.ExpenseTracker.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TransactionServiceImpl implements TransactionService{

    private final UserRepository userRepo;
    private final CategoryRepo categoryRepo;
    private final TransactionRepo transactionRepo;

    @Override
    public void saveCategoryToDB(String categoryName, String type) {
        String dbName = categoryName.toLowerCase();
        if(type.equals("expense")) {
            ((ExpenseCategoryRepository)categoryRepo).saveAndFlush(
                    new ExpenseCategory(dbName, getUser().get()));
        } else{
            ((IncomeCategoryRepository)categoryRepo).saveAndFlush(
                    new IncomeCategory(dbName, getUser().get()));
        }
    }

    @Override
    public  void saveTransactionToDB(LocalDate date, Double expenseAmount,
                                     String categoryName, String description,
                                     String categoryType) {
        categoryName = categoryName.toLowerCase();

        if(categoryType.equals("expense")) {
            ((ExpenseTransactionRepository)categoryRepo).saveAndFlush(
                    new ExpenseTransaction(date, expenseAmount, categoryName, description, getUser().get()));

        } else{
            ((IncomeTransactionRepository)categoryRepo).saveAndFlush(
                    new IncomeTransaction(date, expenseAmount, categoryName, description, getUser().get()));
        }
    }

    @Override
    public int numberOfTransactionsByCategory(String categoryName, CategoryRepo cRepo, TransactionRepo tRepo) {
        return 0;
    }

    @Override
    public Optional<?> getCategory(String type, String categoryName) {
        Optional<?> category;

        if(type.equals("expense"))
            category = ((ExpenseCategoryRepository)categoryRepo).findAllCategories().stream()
                    .filter(c -> c.getUser().getUserId().equals(getUser().get().getUserId()))
                    .filter(( c -> c.getCategoryName().equals(categoryName.toLowerCase())))
                    .findFirst();
        else
            category = ((IncomeCategoryRepository)categoryRepo).findAllCategories().stream()
                    .filter(c -> c.getUser().getUserId().equals(getUser().get().getUserId()))
                    .filter(( c -> c.getCategoryName().equals(categoryName.toLowerCase())))
                    .findFirst();

        if(category.isEmpty())
            throw  new ResponseStatusException(NOT_FOUND, "Category with this name doens't exist.");

        return category;
    }

    @Override
    public Set<?> getCategories(String type) {
        Set<?> result;

        if(type.equals("expense"))
            result = Set.of(((ExpenseCategoryRepository)categoryRepo)
                    .findAllCategories().stream()
                    .filter(c -> c.getUser().getUserId().equals(getUser().get().getUserId())));
        else{
            result = Set.of(((IncomeCategoryRepository)categoryRepo)
                    .findAllCategories().stream()
                    .filter(c -> c.getUser().getUserId().equals(getUser().get().getUserId())));
        }

        if(result.size() == 0)
            throw new ResponseStatusException(NOT_FOUND, "There are no registered categories.");

        return result;
    }

    @Override
    public Optional<?> getTransactionById(String type, Long transactionId) {
        Optional<User> user = getUser();
        if(user.get().getUsername().equals("admin")){
            if(type.equals("expense")){
                return ((ExpenseTransactionRepository)transactionRepo).findById(transactionId);
            }else{
                return ((IncomeTransactionRepository)transactionRepo).findById(transactionId);
            }
        }
        return user.get()
                .getExpenseTransactions().stream()
                .filter(transaction -> transaction.getExpenseTransactionId().equals(transactionId))
                .findFirst();
    }

    @Override
    public Page<?> getTransactionsByCategoryAndUsername(Pageable pageable, String type, String categoryName) {
        Page<?> result;
        categoryName = categoryName.toLowerCase();
        if(type.equals("expense")){
            result = ((ExpenseTransactionRepository)transactionRepo).filterTransactionsByUsernameAndCategory(pageable,
                    getUsernameByAuthentication(), categoryName);
        } else{
            result = ((IncomeTransactionRepository)transactionRepo).filterTransactionsByUsernameAndCategory(pageable,
                    getUsernameByAuthentication(), categoryName);
        }
        if(result.isEmpty())
            throw new ResponseStatusException(NOT_FOUND, "You don't haven't made any transactions in this category!");

        return result;
    }

    @Override
    public Page<?> getTransactionByDate(Pageable pageable, String date, String type) {
        LocalDate dateTime = LocalDate.parse(date);
        Page<?> result;

        if(type.equals("expense")){
            result = ((ExpenseTransactionRepository)transactionRepo)
                    .filteredTransactionsByDate(pageable, getUsernameByAuthentication(), dateTime);
        } else{
            result = ((IncomeTransactionRepository)transactionRepo)
                    .filteredTransactionsByDate(pageable, getUsernameByAuthentication(), dateTime);
        }

        if(result.isEmpty())
            throw new ResponseStatusException(NOT_FOUND, "You didn't register any transactions on this date!");

        return result;
    }

    @Override
    public Page<?> getAllUserTransactions(Pageable pageable, String type, TransactionRepo repo) {
        Optional<User> user = getUser();
        Page<?> result;
        if(type.equals("expense")){
            result = ((ExpenseTransactionRepository)transactionRepo).filterTransactionsByUsername(pageable, user.get().getUsername());
        }else{
            result = ((IncomeTransactionRepository)transactionRepo).filterTransactionsByUsername(pageable, user.get().getUsername());
        }

        if(result.isEmpty())
            throw new ResponseStatusException(NOT_FOUND, "You don't haven't made any transactions!");

        return result;
    }

    @Override
    public void addCategory(String categoryName, String type, CategoryRepo repo) {
        Optional<User> user = getUser();
        String dbName = categoryName.toLowerCase();
        Optional<?> category;
        if(type.equals("expense")){
            category = ((ExpenseCategoryRepository)repo).fetchCategoryByCategoryNameAndUser(dbName, user.get());
        }else if(type.equals("income")){
            category = ((IncomeCategoryRepository)repo).fetchCategoryByCategoryNameAndUser(dbName, user.get());
        } else{
            throw new ResponseStatusException(BAD_REQUEST, "Please enter a valid category type. Either income/expense.");
        }

        if(category.isPresent())
            throw new ResponseStatusException(BAD_REQUEST, "Category with name: " + categoryName + ", already exists.");

        if(type.equals("expense")) {
            ExpenseCategory expenseCategory = new ExpenseCategory(dbName, user.get());
            user.get().addExpenseCategoryToUser(expenseCategory);
            ((ExpenseCategoryRepository)repo).save(expenseCategory);
        } else{
            IncomeCategory incomeCategory = new IncomeCategory(categoryName, user.get());
            user.get().addIncomeCategoryToUser(incomeCategory);
            ((IncomeCategoryRepository)repo).save(incomeCategory);
        }
    }

    @Override
    public void addTransaction(String categoryType, String date,
                               Double transactionAmount, String categoryName,
                               String description, TransactionRepo tRepo, CategoryRepo cRepo) {
        Optional<User> user = getUser();
        LocalDate curDate;
        try{
//            Gets the date of the transaction:
            curDate = LocalDate.parse(date);
            categoryName = categoryName.toLowerCase();

            if(categoryType.equals("expense")){
               ExpenseCategoryRepository expenseCategoryRepo = (ExpenseCategoryRepository) cRepo;
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
                ((ExpenseTransactionRepository)tRepo).save(expenseTransaction);

            } else if(categoryType.equals("income")){
                IncomeCategoryRepository incomeCategoryRepository = (IncomeCategoryRepository) cRepo;
                IncomeTransaction incomeTransaction = new IncomeTransaction(curDate, transactionAmount,
                        categoryName, description, user.get());
                Optional<IncomeCategory> category = incomeCategoryRepository.fetchCategoryByCategoryNameAndUser(categoryName, user.get());

                if(category.isEmpty()){
                    IncomeCategory newCategory = new IncomeCategory(categoryName, user.get());
                    incomeCategoryRepository.save(newCategory);
                    incomeTransaction.setIncomeCategory(newCategory);
                } else{
                    incomeTransaction.setIncomeCategory(category.get());
                }
                user.get().addIncomeAmountToUser(incomeTransaction.getIncomeAmount());
                ((IncomeTransactionRepository)tRepo).save(incomeTransaction);

            } else{
                throw new ResponseStatusException(BAD_REQUEST, "Please enter a valid category type. Either income/expense.");
            }

        } catch (DateTimeException dte){
            throw new ResponseStatusException(NOT_ACCEPTABLE, "Please, provide a correct format for the date of the transaction.(YYYY-MM-DD)");
        }
    }

    @Override
    public boolean transactionExists(String type, Long transactionId) {
        User user = getUser().get();
        if(type.equals("expense")){
            return ((ExpenseTransactionRepository)transactionRepo)
                    .existsExpenseTransactionByUserAndExpenseTransactionId(user, transactionId);
        }
        return ((IncomeTransactionRepository)transactionRepo)
                .existsIncomeTransactionByUserAndIncomeTransactionId(user, transactionId);
    }

    @Override
    public boolean categoryExists(String type, String categoryName) {
        User user = getUser().get();
        if(type.equals("expense")){
            return ((ExpenseCategoryRepository)categoryRepo)
                    .existsExpenseCategoryByCategoryNameAndUser(categoryName, user);
        }
        return ((IncomeCategoryRepository)categoryRepo)
                .existsIncomeCategoryByCategoryNameAndUser(categoryName, user);
    }

    @Override
    public void deleteAllUserTransactions(String type){
        User user = getUser().get();

        if(type.equals("expense")){
            if(user.getExpenseTransactions().size() == 0)
                throw new ResponseStatusException(NOT_FOUND,"There are no expense transactions made by " + user.getUsername());
            ((ExpenseTransactionRepository)transactionRepo).deleteExpenseTransactionsByUser(user);
        }else{
            if(user.getIncomeTransactions().size() == 0)
                throw new ResponseStatusException(NOT_FOUND,"There are no income transactions made by " + user.getUsername());
            ((IncomeTransactionRepository)transactionRepo).deleteIncomeTransactionsByUser(user);
        }
    }

    @Override
    public void deleteTransactionById(Pageable pageable, String type, Long transactionId){
        User user = getUser().get();
        if(type.equals("expense")){
            Optional<ExpenseTransaction> transaction = ((ExpenseTransactionRepository)transactionRepo)
                    .filteredTransactionsById(pageable, transactionId, getUsernameByAuthentication());
            if(transaction.isEmpty())
                throw new ResponseStatusException(BAD_REQUEST,"Expense transaction with id: " + transactionId + " doesn't exist!");

            ((ExpenseTransactionRepository)transactionRepo).delete(transaction.get());
        }else{
            Optional<IncomeTransaction> transaction = ((IncomeTransactionRepository)transactionRepo)
                    .filteredTransactionsById(pageable, transactionId, getUsernameByAuthentication());
            if(transaction.isEmpty())
                throw new ResponseStatusException(BAD_REQUEST,"Expense transaction with id: " + transactionId + " doesn't exist!");

            ((IncomeTransactionRepository)transactionRepo).delete(transaction.get());
        }

    }

    @Override
    public  void deleteTransactionsByCategory(String type, String categoryName) {
        User user = getUser().get();
        if(type.equals("expense")){
            Optional<ExpenseCategory> category = ((ExpenseCategoryRepository)categoryRepo).fetchCategoryByCategoryNameAndUser
                    (categoryName.toLowerCase(), user);
            if(category.isEmpty())
                throw new ResponseStatusException(NOT_FOUND, "Category with this name doesn't exist.");
            ((ExpenseTransactionRepository)transactionRepo)
                    .deleteExpenseTransactionsByExpenseCategoryAndUser(category.get(), user);
        }else{
            Optional<IncomeCategory> category = ((IncomeCategoryRepository)categoryRepo).fetchCategoryByCategoryNameAndUser
                    (categoryName.toLowerCase(), user);
            if(category.isEmpty())
                throw new ResponseStatusException(NOT_FOUND, "Category with this name doesn't exist.");
            ((IncomeTransactionRepository)transactionRepo)
                    .deleteIncomeTransactionsByIncomeCategoryAndUser(category.get(), user);
        }
    }

    @Override
    public void deleteCategory(String categoryName, String type) {
        User user = getUser().get();
        if(type.equals("expense")){
            ((ExpenseCategoryRepository)categoryRepo).deleteExpenseCategoryByUserAndCategoryName(
                    user, categoryName.toLowerCase());
        }else{
            ((IncomeCategoryRepository)categoryRepo).deleteIncomeCategoryByUserAndCategoryName(
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
