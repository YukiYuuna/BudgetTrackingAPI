package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.Role;
import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.exception.BadRequestException;
import com.rigel.ExpenseTracker.exception.NotFoundException;
import com.rigel.ExpenseTracker.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
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

        if(Objects.equals(user.getUsername(), "admin")) {
            addRoleInDB(user, Role.ROLE_ADMIN);
        } else{
            addRoleInDB(user, Role.ROLE_USER);
        }

        return userRepo.save(user);
    }

    @Override
    public Integer numberOfUsers() {
        return userRepo.findAll().size();
    }

    @Override
    public Double totalBudgetOfUser() {
        return userExists(getUsernameByAuthentication()).get().getCurrentBudget();
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
    public User getUser() {
        Optional<User> user = userExists(getUsernameByAuthentication());
        return user.get();
    }

    @Override
    public User getUserByUsername(String username) {
        User user = userRepo.findByUsername(username);
        if(user == null)
            throw new NotFoundException("User " + username + " doesn't exist.");
        return userRepo.findByUsername(username);
    }

    @Override
    public Page<User> getUsers(Pageable pageable) {
        return userRepo.filterUsers(pageable);
    }

    @Override
    public boolean usernameExists() {
        return userRepo.existsByUsername(getUsernameByAuthentication());
    }

    @Override
    public Optional<User> getOptionalUser() {
        return userRepo.findUserByUsername(getUsernameByAuthentication());
    }

    @Override
    public void deleteUser() {
        userRepo.delete(
                userExists(getUsernameByAuthentication())
                        .get());
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    private Optional<User> userExists(String username){
        Optional<User> user = userRepo.findUserByUsername(username);
        if (user.isEmpty())
            throw new NotFoundException("User with username: " + username + " doesn't exist.");
        return user;
    }

    private void addRoleInDB(User user, String roleName){
        Set<Role> roles = new HashSet<>();
        if(roleRepo.findAll().stream().noneMatch(role -> role.getRoleName().equals(roleName))) {
            roles.add(new Role(roleName));
        }
        else {
            roles.add(roleRepo.findByRoleName(roleName));
        }
        user.setRoles(roles);
    }

}
