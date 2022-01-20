package com.rigel.ExpenseTracker.controllers;

import com.google.common.base.Optional;
import com.rigel.ExpenseTracker.AbstractTest;
import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.repositories.RoleRepo;
import com.rigel.ExpenseTracker.repositories.UserRepository;
import com.rigel.ExpenseTracker.service.UserService;
import com.rigel.ExpenseTracker.service.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest extends AbstractTest {

    @Mock private UserRepository mockUserRepo;
    @Mock private RoleRepo mockRoleRepo;
    @Mock private PasswordEncoder mockPasswordEncoder;
    @Mock private UserService userServiceTest;
    @Mock private UserController userController;

    @Autowired WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
//        autoCloseable = MockitoAnnotations.openMocks(this);
        userServiceTest = new UserServiceImpl(mockUserRepo, mockRoleRepo, mockPasswordEncoder);
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @BeforeAll
    public static void beforeAll() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getDetails()).thenReturn(
                new User("admin", "admin", "Koko", "Borimechkov", "koko@gmail.com", 9000.0));
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("admin");
    }

//    @BeforeEach
//    void init() {
//        User admin = new User("admin", "admin", "Koko", "Borimechkov", "koko@gmail.com", 9000.0);
//        User user1 = new User("ivan", "ivan", "Ivan", "Duhov", "ivanDuhov@gmail.com", 100000.0);
//        User user2 = new User("ivo", "ivo", "Ivo", "Petkov", "ipetkov@gmail.com", 800.0);
//        mockUserRepo.save(admin);
//        mockUserRepo.save(user1);
//        mockUserRepo.save(user2);
//    }

    @AfterEach
    void tearDown() {
        mockUserRepo.deleteAll();
    }

    @Test
    void getUserInfoTest() throws Exception
    {}

    @Test
    void getUserById() {
    }

    @Test
    void getAllUsers() {
    }

    @Test
    void saveRole() {
    }

    @Test
    void addRoleToUser() {
    }

    @Test
    void modifyUserInfo() {
    }

    @Test
    void deleteUser() {
    }

}