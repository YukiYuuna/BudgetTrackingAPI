package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class TransactionServiceImpl implements TransactionService{

    @Autowired
    UserRepository userRepo;

    @Override
    public void saveTransactionCategoryToDB(String categoryName, TransactionService service) {

    }

    @Override
    public void saveTransactionTransactionToDB(Optional<?> transaction, TransactionService service) {

    }

    @Override
    public int numberOfTransactionsByCategory(String categoryName, TransactionService service) {
        return 0;
    }

    @Override
    public Optional<?> getOptionalTransactionCategory(String categoryName) {
        return Optional.empty();
    }

    @Override
    public Optional<?> getTransactionCategory(String categoryName, TransactionRepo repo) {
        Optional<?> category = getOptionalTransactionCategory(categoryName.toLowerCase());

        if(category.isEmpty())
            throw new ResponseStatusException(NOT_FOUND, "A category with this name doesn't exist!");

        return (Optional<?>) category.get();
    }

    @Override
    public Set<?> getTransactionCategories(TransactionRepo repo) {
        User user = getUser().get();

        Set<?> result  = (Set<?>)  repo.findAllCategories().stream().filter(c -> c.getUserId().equals(user.getUserId()));

        if(result.isEmpty())
            throw new ResponseStatusException(NOT_FOUND, "There are no registered income categories.");

        return result;
    }

    @Override
    public HashMap<String, Object> getAllUserTransactions(Pageable pageable, TransactionService service) {
        return null;
    }

    @Override
    public Optional<?> getTransactionById(Long transactionId, TransactionService service) {
        return Optional.empty();
    }

    @Override
    public Page<?> getTransactionsByCategoryAndUsername(Pageable pageable, String categoryName, TransactionService service) {
        return null;
    }

    @Override
    public HashMap<String, Object> getTransactionTransactionByDate(String date, TransactionService service) {
        return null;
    }

    @Override
    public Page<?> getTransactionTransactions(Pageable pageable, TransactionService service) {
        return null;
    }

    @Override
    public void addTransactionCategory(String categoryName, TransactionService service) {

    }

    @Override
    public void addTransactionTransaction(String date, Double TransactionAmount, String categoryName, String description, TransactionService service) {

    }

    @Override
    public boolean TransactionTransactionExists(Long transactionId, TransactionService service) {
        return false;
    }

    @Override
    public boolean TransactionCategoryExists(String categoryName, TransactionService service) {
        return false;
    }

    @Override
    public void deleteTransactionByUser(TransactionService service) {

    }

    @Override
    public void deleteTransactionById(Long transactionId, TransactionService service) {

    }

    @Override
    public void deleteTransactionsByCategory(String categoryName, TransactionService service) {

    }

    @Override
    public void deleteTransactionCategory(String categoryName, TransactionService service) {

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

    @Override
    public Object doesCategoryExist(User user, String categoryName) {
        return null;
    }
}
