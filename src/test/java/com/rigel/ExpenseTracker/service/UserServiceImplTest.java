package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.repositories.RoleRepo;
import com.rigel.ExpenseTracker.repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepo;
    @Mock private RoleRepo roleRepo;
    @Mock private PasswordEncoder passwordEncoder;
//    private AutoCloseable autoCloseable;
    private UserService underTest;

//    runs before each test
    @BeforeEach
    void setUp() {
//        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new UserServiceImpl(userRepo, roleRepo, passwordEncoder);
    }

//    @AfterEach
//    void tearDown() throws Exception {
//        close the resource after the tests have finished:
//        autoCloseable.close();
//    }

    @Test
    @Disabled
    void loadUserByUsername() {
    }

    @Test
    void saveUser() {
    }

    @Test
    void clearSave() {
    }

    @Test
    void saveRole() {
    }

    @Test
    void addRoleToUser() {
    }

    @Test
    void numberOfUsers() {
    }

    @Test
    void getUser() {
    }

    @Test
    void getOptionalUser() {
    }

    @Test
    void getUserById() {
    }

    @Test
    void canGetAllDBUsers() {
//        when
        underTest.getAllDBUsers();

//        then
        /*
         *       basically we want to say that the userRepo mock was invoked using the method findAll(),
         *       when using the getAllDBUsers in the serviceImpl
         */
        verify(userRepo).findAll();
    }

    @Test
    void getUsers() {
    }

    @Test
    void usernameExists() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void saveUserDataAndFlush() {
    }
}