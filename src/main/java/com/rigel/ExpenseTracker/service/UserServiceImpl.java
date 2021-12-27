package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.Role;
import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.exception.BadRequestException;
import com.rigel.ExpenseTracker.exception.NotFoundException;
import com.rigel.ExpenseTracker.repositories.ExpenseCategoryRepository;
import com.rigel.ExpenseTracker.repositories.IncomeCategoryRepository;
import com.rigel.ExpenseTracker.repositories.RoleRepo;
import com.rigel.ExpenseTracker.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepo;
    private final RoleRepo roleRepo;
    private final ExpenseCategoryRepository expensesRepo;
    private final IncomeCategoryRepository incomeRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepo.findUserByUsername(username);
        if(user.isEmpty()) {
            log.error("User not found in the database");;
            throw new NotFoundException("User with this username not found in the database!");
        }

        log.info("User found in the database: {}", username);
        return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(),
                buildSimpleGrantedAuthorities(user.get().getRoles()));
    }

    @Override
    public User saveUser(User user) {
        log.info("User was saved to the database successfully!");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Role saved to the database successfully!");
        if(roleRepo.existsByRoleName(role.getRoleName()))
            throw new BadRequestException("This role already exists in the DB!");
        return roleRepo.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        Optional<User> user = userRepo.findUserByUsername(username);
        if(user.isEmpty())
            throw new NotFoundException("User with this username doesn't exist!");

        Role role = roleRepo.findByRoleName(roleName);
        user.get().getRoles().add(role);
    }

    @Override
    public ExpenseCategory saveCategory(ExpenseCategory expenseCategory) {
        return expensesRepo.save(expenseCategory);
    }

    @Override
    public void addExpenseCategory(String username, String categoryName) {
        if(!userRepo.existsByUsername(username))
            throw new NotFoundException("User with this username doesn't exist!");
        User user = userRepo.findByUsername(username);
        ExpenseCategory category = expensesRepo.findByCategoryName(categoryName);
        user.getExpenseCategories().add(category);
    }

    @Override
    public User getUser(String username) {
        if(!userRepo.existsByUsername(username))
            throw new NotFoundException("User with this username doesn't exist!");
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

    @Override
    public void saveUserDataAndFlush(User user) {
        userRepo.saveAndFlush(user);
    }

    private static List<SimpleGrantedAuthority> buildSimpleGrantedAuthorities(final Set<Role> roles) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        }
        return authorities;
    }
}
