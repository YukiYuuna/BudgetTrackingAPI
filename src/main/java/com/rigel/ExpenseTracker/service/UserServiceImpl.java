package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.Role;
import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.*;

import static org.springframework.http.HttpStatus.*;

@Service
@Transactional
@Slf4j
@Component
public class UserServiceImpl  implements UserService, UserDetailsService {

    private final UserRepository userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepo, RoleRepo roleRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepo.findUserByUsername(username);
        if(user.isEmpty() && !username.equals("NONE_PROVIDED")) {
            throw new ResponseStatusException(NOT_FOUND, "User with this username not found in the database!");
        }

        return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(),
                buildSimpleGrantedAuthorities(user.get().getRoles()));
    }

    @Override
    public User saveUser(User user) {
        log.info("User was saved to the database successfully!");
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Set<Role> role = new HashSet<>();
        role.add(roleRepo.findByRoleName(Role.ROLE_USER));

        user.setRoles(role);

        return userRepo.save(user);
    }

    @Override
    public void clearSave(User user){
        userRepo.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Role saved to the database successfully!");
        if(roleRepo.existsByRoleName(role.getRoleName()))
            throw new ResponseStatusException(BAD_REQUEST, "This role already exists in the DB!");
        return roleRepo.save(role);
    }

    @Override
    public void addRoleToUser(String roleName) {
        Role role = roleRepo.findByRoleName(roleName);
        getUser().getRoles().add(role);
    }

    @Override
    public int numberOfUsers(){
        return userRepo.findAll().size();
    }

    @Override
    public User getUser(){
        return getOptionalUser().get();
    }

    @Override
    public Optional<User> getOptionalUser() {
        Optional<User> user = userRepo.findUserByUsername(getUsernameByAuthentication());

        if (user.isPresent()) {
            return user;
        }
        else {
            throw new ResponseStatusException(BAD_REQUEST, "Sorry, something went wrong.");
        }
    }

    @Override
    public User getUserById(Long userId) {
        Optional<User> user = userRepo.findUserByUserId(userId);
        if(user.isEmpty())
            throw new ResponseStatusException(NOT_FOUND, "No user with id " + userId + " found in the DB!");
        return user.get();
    }

    @Override
    public List<User> getAllDBUsers() {
        return userRepo.findAll();
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
    public void deleteUser() {
        userRepo.delete(getUser());
    }

    @Override
    public void saveUserDataAndFlush(User user) {
        userRepo.saveAndFlush(user);
    }

    static List<SimpleGrantedAuthority> buildSimpleGrantedAuthorities(final Set<Role> roles) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        }
        return authorities;
    }

    String getUsernameByAuthentication(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
