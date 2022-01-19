package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.repositories.RoleRepo;
import com.rigel.ExpenseTracker.repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContext;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository mockUserRepo;
    @Mock private RoleRepo mockRoleRepo;
    @Mock private PasswordEncoder mockPasswordEncoder;
    @Mock private UserService mockUserService;

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

    @BeforeEach
    void init() {
        User user1 = new User("ivan", "ivan", "Ivan", "Duhov", "ivanDuhov@gmail.com", 100000.0);
        User user2 = new User("ivo", "ivo", "Ivo", "Petkov", "ipetkov@gmail.com", 800.0);
        mockUserRepo.save(user1);
        mockUserRepo.save(user2);
    }

    //    runs before each test
    @BeforeEach
    void setUp() {
//        autoCloseable = MockitoAnnotations.openMocks(this);
        mockUserService = new UserServiceImpl(mockUserRepo, mockRoleRepo, mockPasswordEncoder);
    }

    @AfterEach
    void tearDown(){
        mockUserRepo.deleteAll();
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
    void getOptionalUserTest() {
    }

    @Test
    void getUserByIdTest() {
    }

    @Test
    void getAllDBUsersTest() {
//        when
        mockUserService.getAllDBUsers();

//        then
        /*
         *       basically we want to say that the userRepo mock was invoked using the method findAll(),
         *       when using the getAllDBUsers in the serviceImpl
         */
        verify(mockUserRepo).findAll();
    }

    @Test
    void getUsersTest() {

    }

    @Test
    void usernameExistsTest() {
        when(mockUserRepo.existsByUsername("admin")).thenReturn(true);
        assertTrue(mockUserService.usernameExists());
    }

    @Test
    void deleteUser() {
    }

    @Test
    void saveUserDataAndFlush() {
    }
}