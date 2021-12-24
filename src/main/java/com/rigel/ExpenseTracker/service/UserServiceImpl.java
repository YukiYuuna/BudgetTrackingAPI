package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.repositories.ExpenseCategoryRepository;
import com.rigel.ExpenseTracker.repositories.IncomeCategoryRepository;
import com.rigel.ExpenseTracker.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserRepository userRepo;
    private final ExpenseCategoryRepository expensesRepo;
    private final IncomeCategoryRepository incomeRepo;

    @Override
    public User saveUser(User user) {
        return userRepo.save(user);
    }

    @Override
    public ExpenseCategory saveCategory(ExpenseCategory expenseCategory) {
        return expensesRepo.save(expenseCategory);
    }

    @Override
    public void addCategory(String username, String categoryName) {
        User user = userRepo.findByUsername(username);
        ExpenseCategory category = expensesRepo.findByCategoryName(categoryName);

        user.getExpenseCategories().add(category);
    }

    @Override
    public User getUser(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public List<User> getUsers() {
        return userRepo.findAll();
    }

    @Override
    public boolean usernameExists(String username) {
        return userRepo.existsByUsername(username);
    }

    @Override
    public Page<User> getFilteredUsers(Pageable pageable, String username) {
        return userRepo.filterUsers(pageable,username);
    }

    @Override
    public Optional<User> getByUsername(String username) {
        return userRepo.findUserByUsername(username);
    }

    @Override
    public void deleteUser(String username) {
        userRepo.deleteUserByUsername(username);
    }
}
