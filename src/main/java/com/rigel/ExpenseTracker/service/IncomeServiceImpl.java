package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.*;
import com.rigel.ExpenseTracker.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class IncomeServiceImpl extends Services implements IncomeService{

    private final UserRepository userRepo;
    private final IncomeCategoryRepository incomeCategoryRepo;
    private final IncomeTransactionRepository incomeTransactionRepo;

    @Override
    public void saveIncomeCategoryToDB(String categoryName) {
        String dbName = categoryName.toLowerCase();
        Optional<IncomeCategory> incomeCategory = getOptionalIncomeCategory(dbName);
        if(incomeCategory.isEmpty()){
            IncomeCategory newCategory = new IncomeCategory(dbName);
            newCategory.setUser(userExists(getUsernameByAuthentication()).get());
            incomeCategoryRepo.saveAndFlush(newCategory);
        }
    }

    @Override
    public void saveIncomeTransactionToDB(IncomeTransaction transaction) {
        incomeTransactionRepo.save(transaction);
    }

    @Override
    public int numberOfTransactionsByCategory(String categoryName) {
        Optional<User> user = userExists(getUsernameByAuthentication());
        if(user.get().getIncomeCategories() == null)
            throw new ResponseStatusException(BAD_REQUEST, "User has no assigned categories.");

        Optional<IncomeCategory> incomeCategory = user.get().getIncomeCategories().stream().filter(c -> c.getCategoryName().equals(categoryName)).findFirst();
        if(incomeCategory.isEmpty())
            throw new ResponseStatusException(NOT_FOUND, "Category with this name doesn't exist in the DB.");
        else{
            if(incomeCategory.get().getIncomeTransactions() == null)
                return  0;
            return incomeCategory.get().getIncomeTransactions().size();
        }
    }

    @Override
    public Optional<IncomeCategory> getOptionalIncomeCategory(String categoryName) {
        Optional<User> user = userExists(getUsernameByAuthentication());
        return user.get()
                .getIncomeCategories().stream()
                .filter(c -> c.getCategoryName().equals(categoryName))
                .findAny();
    }

    @Override
    public IncomeCategory getIncomeCategory(String categoryName) {
        Optional<IncomeCategory> category = getOptionalIncomeCategory(categoryName.toLowerCase());
        if(category.isEmpty())
            throw new ResponseStatusException(NOT_FOUND, "A category with this name doesn't exist!");

        return category.get();
    }

    @Override
    public Set<IncomeCategory> getIncomeCategories() {
        Optional<User> user = userExists(getUsernameByAuthentication());
        if(user.get().getIncomeCategories() == null)
            throw new ResponseStatusException(NOT_FOUND, "There are no registered income categories.");
        return user.get().getIncomeCategories();
    }

    @Override
    public HashMap<String, Object> getAllUserTransactions(Pageable pageable) {
        String username = getUsernameByAuthentication();
        HashMap<String, Object> result = new LinkedHashMap<>();
        Page<IncomeTransaction> transactions = incomeTransactionRepo.filterTransactionsByUsername(pageable, username);

        result.put("username", username);
        result.put("totalTransactions", transactions.getTotalElements());
        result.put("totalPages", transactions.getTotalPages());
        result.put("transactions", transactions.getContent());

        return result;
    }

    @Override
    public Optional<IncomeTransaction> getTransactionById(Long transactionId) {
        Optional<User> user = userExists(getUsernameByAuthentication());
        return user.get()
                .getIncomeTransactions().stream()
                .filter(transaction -> transaction.getIncomeTransactionId().equals(transactionId))
                .findFirst();
    }

    @Override
    public Page<IncomeTransaction> getTransactionsByCategoryAndUsername(Pageable pageable, String categoryName) {
        Optional<User> user = userExists(getUsernameByAuthentication());
        return incomeTransactionRepo.filterTransactionsByUsernameAndCategory(pageable, categoryName, user.get().getUsername());
    }

    @Override
    public HashMap<String, Object> getIncomeTransactionByDate(String date) {
        try{
            Optional<User> user = userExists(getUsernameByAuthentication());
            HashMap<String, Object> result = new LinkedHashMap<>();

            List<IncomeTransaction> transactions = user.get()
                    .getIncomeTransactions().stream()
                    .filter(t -> t.getDate().toString().equals(date))
                    .collect(Collectors.toList());

            if(transactions.size() == 0)
                throw new ResponseStatusException(NOT_FOUND,"No income transactions have been made on this date - " + date);

            result.put("username", user.get().getUsername());
            result.put("date", date);
            result.put("totalSpent", transactions.stream()
                    .map(IncomeTransaction::getIncomeAmount)
                    .reduce(0.0, Double::sum));

            result.put("transactions", transactions);
            return result;
        } catch (DateTimeException e){
            throw new ResponseStatusException(NOT_ACCEPTABLE, "Please, provide a valid date format: YYYY-MM-DD");
        }
    }

    @Override
    public Page<IncomeTransaction> getIncomeTransactions(Pageable pageable) {
        return incomeTransactionRepo.filteredTransactions(pageable);
    }

    @Override
    public void addIncomeCategory(String categoryName) {
        Optional<User> user = userExists(getUsernameByAuthentication());
        Set<IncomeCategory> incomeCategories = user.get().getIncomeCategories();

        if(incomeCategories.size() > 0) {
            boolean categoryExists = incomeCategories.stream().anyMatch(category -> category.getCategoryName().equals(categoryName));
            if (categoryExists)
                throw new ResponseStatusException(BAD_REQUEST, "Category with name: " + categoryName + ", already exists.");
        }

        IncomeCategory incomeCategory = new IncomeCategory(categoryName, user.get());
        user.get().addIncomeCategoryToUser(incomeCategory);
        incomeCategoryRepo.save(incomeCategory);
    }

    @Override
    public void addIncomeTransaction(String date, Double incomeAmount, String categoryName, String description) {
        Optional<User> user = userExists(getUsernameByAuthentication());

        LocalDate curDate;
        try{
            curDate = LocalDate.parse(date);

            IncomeTransaction incomeTransaction = new IncomeTransaction(curDate, incomeAmount, categoryName, description, user.get());

            Optional<IncomeCategory> category = incomeCategoryRepo.findIncomeCategoryByCategoryNameAndUser(categoryName, user.get());

            if(category.isEmpty()){
                IncomeCategory incomeCategory = new IncomeCategory(categoryName, user.get());
                incomeCategoryRepo.save(incomeCategory);
                incomeTransaction.setIncomeCategory(incomeCategory);
            } else{
                incomeTransaction.setIncomeCategory(category.get());
            }

            user.get().addIncomeAmountToUser(incomeTransaction.getIncomeAmount());

            incomeTransactionRepo.save(incomeTransaction);
        } catch (DateTimeException dte){
            throw new ResponseStatusException(NOT_ACCEPTABLE, "Please, provide a correct format for the date of the transaction.(YYYY-MM-DD)");
        }
    }

    @Override
    public boolean incomeTransactionExists(Long transactionId) {
        if(incomeTransactionRepo.existsIncomeTransactionByUserAndIncomeTransactionId(
                userExists(getUsernameByAuthentication()).get(), transactionId))
            return true;
        return false;
    }

    @Override
    public boolean incomeCategoryExists(String categoryName) {
        boolean exists = userExists(getUsernameByAuthentication()).get()
                .getIncomeCategories()
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
            throw new ResponseStatusException(NOT_FOUND, "There are no transactions made by " + user.get().getUsername());

        incomeTransactionRepo.deleteIncomeTransactionsByUser(user.get());
    }

    @Override
    public void deleteTransactionById(Long transactionId) {
        Optional<User> user = userExists(getUsernameByAuthentication());
        Optional<IncomeTransaction> incomeTransaction =  user.get()
                .getIncomeTransactions().stream()
                .filter(transaction -> transaction.getIncomeTransactionId().equals(transactionId))
                .findFirst();
        if(incomeTransaction.isEmpty())
            throw new ResponseStatusException(NOT_FOUND, "Transaction with id: " + transactionId + " doesn't exist!");

        incomeTransactionRepo.delete(incomeTransaction.get());
    }

    @Override
    public void deleteTransactionsByCategory(String categoryName) {
        Optional<User> user = userExists(getUsernameByAuthentication());
        incomeTransactionRepo.deleteIncomeTransactionsByIncomeCategoryAndUser(
                doesCategoryExist(user.get(),categoryName.toLowerCase()),
                user.get()
        );
    }

    @Override
    public void deleteIncomeCategory(String categoryName) {
        deleteTransactionsByCategory(categoryName);
        incomeCategoryRepo.deleteIncomeCategoryByUserAndAndCategoryName(
                userExists(getUsernameByAuthentication()).get(),
                categoryName.toLowerCase()
        );
    }

    @Override
    Optional<User> userExists(String username) {
        Optional<User> user = userRepo.findUserByUsername(username);
        if (user.isEmpty())
            throw new ResponseStatusException(NOT_FOUND, "User with username: " + username + " doesn't exist.");
        return user;
    }

    @Override
    IncomeCategory doesCategoryExist(User user, String categoryName){
        Optional<IncomeCategory> category = user.getIncomeCategories()
                .stream().filter(name -> name.getCategoryName().equals(categoryName)).findFirst();
        if(category.isEmpty())
            throw new ResponseStatusException(NOT_FOUND, "Category with this name doesn't exist.");
        return category.get();
    }
}
