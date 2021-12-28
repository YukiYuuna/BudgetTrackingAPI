package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.IAuthenticationFacade;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private IAuthenticationFacade authenticationFacade;

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
    public Integer numberOfUsers() {
        return userRepo.findAll().size();
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Role saved to the database successfully!");
        if(roleRepo.existsByRoleName(role.getRoleName()))
            throw new BadRequestException("This role already exists in the DB!");
        return roleRepo.save(role);
    }

    @Override
    public void addRoleToUser(String roleName) {
        String username = getUsernameByAuthentication();

        Optional<User> user = userRepo.findUserByUsername(username);
        if(user.isEmpty())
            throw new NotFoundException("User with this username doesn't exist!");

        Role role = roleRepo.findByRoleName(roleName);
        user.get().getRoles().add(role);
    }

    @Override
    public void addExpenseCategory(String categoryName) {
//        getting the username of the logged-in user:
        String username = getUsernameByAuthentication();

        if(!userRepo.existsByUsername(username))
            throw new NotFoundException("User with this username doesn't exist!");

        User user = userRepo.findByUsername(username);
        ExpenseCategory category = expensesRepo.findByCategoryName(categoryName);
        user.getExpenseCategories().add(category);
    }

    @Override
    public User getUser() {
        String username = getUsernameByAuthentication();
        if(!userRepo.existsByUsername(username))
            throw new NotFoundException("User with this username doesn't exist!");

        return userRepo.findByUsername(username);
    }

    @Override
    public Page<User> getUsers(Pageable pageable) {
        String username = getUsernameByAuthentication();
        return userRepo.filterUsers(pageable,username);
    }

    @Override
    public boolean usernameExists() {
        String username = getUsernameByAuthentication();
        return userRepo.existsByUsername(username);
    }

    @Override
    public Optional<User> getOptionalUser() {
        String username = getUsernameByAuthentication();
        return userRepo.findUserByUsername(username);
    }

    @Override
    public void deleteUser() {
        String username = getUsernameByAuthentication();
        Optional<User> user = userRepo.findUserByUsername(username);

        if (user.isEmpty())
            throw new NotFoundException("User with username: " + username + " doesn't exist.");

        userRepo.delete(user.get());
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

    public String getUsernameByAuthentication(){
        return authenticationFacade.getAuthentication().getName();
    }
}
