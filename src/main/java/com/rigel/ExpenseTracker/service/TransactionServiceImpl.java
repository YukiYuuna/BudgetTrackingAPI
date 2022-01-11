package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.IncomeCategory;
import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TransactionServiceImpl implements TransactionService{

    private final UserRepository userRepo;

    @Override
    public void saveCategoryToDB(String categoryName, String type, CategoryRepo repo) {
        String dbName = categoryName.toLowerCase();

        Optional<?> category = getCategory(repo, type, dbName);

        if(category.isEmpty()){
            if(type.equals("expense")) {
                ExpenseCategory newCategory = new ExpenseCategory(dbName, getUser().get());
                ((ExpenseCategoryRepository)repo).saveAndFlush(newCategory);
            } else{
                IncomeCategory newCategory = new IncomeCategory(dbName, getUser().get());
                ((IncomeCategoryRepository)repo).saveAndFlush(newCategory);
            }
        }
    }

    @Override
    public  void saveTransactionToDB(TransactionRepo repo) {
        String dbName = categoryName.toLowerCase();

        Optional<?> category = getCategory(repo, type, dbName);

        if(category.isEmpty()){
            if(type.equals("expense")) {
                ExpenseCategory newCategory = new ExpenseCategory(dbName, getUser().get());
                ((ExpenseCategoryRepository)repo).saveAndFlush(newCategory);
            } else{
                IncomeCategory newCategory = new IncomeCategory(dbName, getUser().get());
                ((IncomeCategoryRepository)repo).saveAndFlush(newCategory);
            }
        }
    }

    @Override
    public int numberOfTransactionsByCategory(String categoryName, CategoryRepo cRepo, TransactionRepo tRepo) {
        return 0;
    }

    @Override
    public Optional<?> getCategory(CategoryRepo repo, String type, String categoryName) {
        Optional<?> category;

        if(type.equals("expense"))
            category = ((ExpenseCategoryRepository)repo).findAllCategories().stream()
                .filter(c -> c.getUser().getUserId().equals(getUser().get().getUserId())).findFirst();
        else
            category = ((IncomeCategoryRepository)repo).findAllCategories().stream()
                    .filter(c -> c.getUser().getUserId().equals(getUser().get().getUserId())).findFirst();

        return category;
    }

    @Override
    public Set<?> getCategories(String type, TransactionRepo repo) {
        Set<?> result;

        if(type.equals("expense"))
            result = Set.of(((ExpenseCategoryRepository)repo)
                    .findAllCategories().stream()
                    .filter(c -> c.getUser().getUserId().equals(getUser().get().getUserId())));
        else{
            result = Set.of(((IncomeCategoryRepository)repo)
                    .findAllCategories().stream()
                    .filter(c -> c.getUser().getUserId().equals(getUser().get().getUserId())));
        }

        if(result.size() == 0)
            throw new ResponseStatusException(NOT_FOUND, "There are no registered income categories.");

        return result;
    }

    @Override
    public HashMap<String, Object> getAllUserTransactions(Pageable pageable, TransactionRepo repo) {
        return null;
    }

    @Override
    public Optional<?> getTransactionById(Long transactionId, TransactionRepo repo) {
        return Optional.empty();
    }

    @Override
    public Page<?> getTransactionsByCategoryAndUsername(Pageable pageable,
                                                        String categoryName,
                                                        CategoryRepo cRepo,
                                                        TransactionRepo tRepo) {
        return null;
    }

    @Override
    public HashMap<String, Object> getTransactionByDate(String date, TransactionRepo repo) {
        return null;
    }

    @Override
    public Page<?> getTransactions(Pageable pageable, TransactionRepo repo) {
        return null;
    }

    @Override
    public void addCategory(String categoryName, CategoryRepo repo) {

    }

    @Override
    public void addTransaction(String date, Double TransactionAmount, String categoryName,
                          String description, TransactionRepo tRepo, CategoryRepo cRepo) {

    }

    @Override
    public boolean transactionExists(Long transactionId, TransactionRepo repo) {
        return false;
    }

    @Override
    public boolean categoryExists(String categoryName, CategoryRepo repo) {
        return false;
    }

    @Override
    public void deleteTransactionByUser(TransactionRepo repo){

    }

    @Override
    public void deleteTransactionById(Long transactionId, TransactionRepo repo){

    }

    @Override
    public  void deleteTransactionsByCategory(String categoryName,CategoryRepo cRepo, TransactionRepo tRepo) {

    }

    @Override
    public void deleteCategory(String categoryName, CategoryRepo repo) {

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
