package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.exception.BadRequestException;
import com.rigel.ExpenseTracker.exception.NotFoundException;
import com.rigel.ExpenseTracker.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService{

    private final ExpenseCategoryRepository expenseCategoryRepo;
    private final ExpenseTransactionRepository expenseTransactionRepo;
    private final IncomeTransactionRepository incomeTransactionRepo;


    @Override
    public ExpenseCategory saveExpenseCategory(ExpenseCategory category) {
//        be careful with the case sensitivity of the name when calling this method
        return expenseCategoryRepo.save(category);
    }

    @Override
    public ExpenseTransaction saveTransaction(ExpenseTransaction transaction) {
        return expenseTransactionRepo.save(transaction);
    }

    @Override
    public void addTransaction(String categoryName, Long transactionId) {
        ExpenseCategory category = expenseCategoryRepo.findExpenseCategoryByCategoryName(categoryName.toLowerCase()).get();
        ExpenseTransaction transaction = expenseTransactionRepo.findExpenseTransactionById(transactionId);
        category.getExpenseTransactions().add(transaction);
    }

    @Override
    public ExpenseCategory getExpenseCategory(String categoryName) {
        Optional<ExpenseCategory> category = expenseCategoryRepo.findExpenseCategoryByCategoryName(categoryName.toLowerCase());
        if(category.isEmpty())
            throw new BadRequestException("A category with this name doesn't exist!");

        return category.get();
    }

    @Override
    public Optional<ExpenseCategory> getOptionalExpenseCategory(String category) {
        return expenseCategoryRepo.findExpenseCategoryByCategoryName(category);
    }

    @Override
    public Set<ExpenseCategory> getExpenseCategories() {
        return expenseCategoryRepo.findAllCategories();
    }

    @Override
    public boolean expenseCategoryExists(String categoryName) {
        return expenseCategoryRepo.existsByCategoryName(categoryName.toLowerCase());
    }

    @Override
    public void deleteExpenseCategory(String categoryName) {
        if (!(expenseCategoryRepo.existsByCategoryName(categoryName)))
            throw new NotFoundException("This category doesn't exist!");

        expenseCategoryRepo.deleteExpenseCategoryByCategoryName(categoryName);
    }

    @Override
    public Optional<ExpenseTransaction> getExpenseTransaction(Long transactionId) {
        return expenseTransactionRepo.findById(transactionId);
    }

    @Override
    public List<ExpenseTransaction> getExpenseTransactions() {
        return expenseTransactionRepo.findAll();
    }

    @Override
    public boolean expenseTransactionExists(Long transactionId) {
        return expenseTransactionRepo.existsById(transactionId);
    }

    @Override
    public void deleteAllUserExpenseTransaction(User user) {
        if(user.getExpenseTransactions().size() == 0)
            throw new NotFoundException("There are no transactions made by " + user.getUsername());

        expenseTransactionRepo.deleteExpenseTransactionsByUser(user);
    }

    @Override
    public Page<ExpenseTransaction> getFilteredTransactions(Pageable pageable, String categoryName) {
        return expenseTransactionRepo.filterTransactionsByCategory(pageable, categoryName);
    }

    @Override
    public List<ExpenseTransaction> getTransactionsByCategory(String categoryName) {
        return expenseTransactionRepo.findExpenseTransactionByExpenseCategory_CategoryName(categoryName);
    }

    @Override
    public List<ExpenseTransaction> getTransactionByUser(User user) {
        return expenseTransactionRepo.findExpenseTransactionByUser(user);
    }

    @Override
    public void deleteTransactionById(Long transactionId) {
        if(!expenseTransactionRepo.existsById(transactionId))
            throw new NotFoundException("Transaction with id: " + transactionId + " doesn't exist!");
        expenseTransactionRepo.deleteById(transactionId);
    }
}
