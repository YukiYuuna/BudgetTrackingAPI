package com.rigel.ExpenseTracker.entities;

import com.rigel.ExpenseTracker.repositories.RoleRepo;
import com.rigel.ExpenseTracker.repositories.UserRepository;
import com.rigel.ExpenseTracker.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Component
@Order
public class DBSeeder {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private  UserRepository userRepo;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private Environment env;

    @Value("spring.jpa.hibernate.ddl-auto")
    String jdbcDriverMode;

    @PostConstruct
    public void seedDbOnCreate() {
        String keyValue = env.getProperty(jdbcDriverMode);

        if (keyValue.equals("create")) {
            User admin = new User("admin", "admin", "admin", "admin", "admin@admin.bg", 1000D);

            roleRepo.save(new Role(Role.ROLE_USER));

            Set<Role> role = new HashSet<>();
            role.add(new Role(Role.ROLE_ADMIN));

            admin.setRoles(role);
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));

            userRepo.save(admin);

        }
    }

}
