package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.repositories.RoleRepo;
import com.rigel.ExpenseTracker.repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContext;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
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
    }

    //    runs before each test
    @BeforeEach
    void setUp() {
//        autoCloseable = MockitoAnnotations.openMocks(this);
        mockUserService = new UserServiceImpl(mockUserRepo, mockRoleRepo, mockPasswordEncoder);
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
        when(mockUserRepo.findAll()).thenReturn(setOfUsers());
        assertThat(mockUserService.numberOfUsers()).isEqualTo(4);
    }

    @Test
    void getOptionalUserTest() {
//        given
        User user1 = new User("ivan", "ivan", "Ivan", "Duhov", "ivanDuhov@gmail.com", 100000.0);
        user1.setUserId(1L);
        String username = String.valueOf(when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("ivan"));
        lenient().when(mockUserRepo.findUserByUsername(username)).thenReturn(Optional.of(user1));
    }

    @Test
    void getUserByIdTest() {
        User user1 = new User("ivan", "ivan", "Ivan", "Duhov", "ivanDuhov@gmail.com", 100000.0);
        when(mockUserRepo.findUserByUserId(1L)).thenReturn(Optional.of(user1));

        assertThat(mockUserService.getUserById(1L)).isEqualTo(user1);
    }

    @Test
    void notUserByIdTest() {
        given(mockUserRepo.findUserByUserId(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> mockUserService.getUserById(1L)).isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"No user with id 1 found in the DB!\"");
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
        Pageable pageable = PageRequest.of(1, 2);
        List<User> users = setOfUsers();
        Page<User> page = new PageImpl<>(users, pageable, users.size());
        when(mockUserRepo.filterUsers(pageable)).thenReturn(page);

        assertThat(mockUserService.getUsers(pageable)).isEqualTo(page);
    }

    @Test
    void usernameExistsTest() {
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("ivan");
        mockUserService.usernameExists();
        verify(mockUserRepo).existsByUsername("ivan");
    }

    @Test
    void deleteUser() {
    }

    @Test
    void saveUserDataAndFlush() {
    }

    private static List<User> setOfUsers(){
        User user1 = new User("ivan", "ivan", "Ivan", "Duhov", "ivanDuhov@gmail.com", 100000.0);
        User user2 = new User("deni", "deni", "Deni", "Duhova", "deniduhova@gmail.com", 9794.0);
        User user3 = new User("koko", "koko", "Koko", "Bor", "kbor@gmail.com", 4643.0);
        User user4 = new User("desi", "desi", "Desi", "Popova", "desippv@gmail.com", 8151125.0);
        return List.of(user1,user2,user3, user4);
    }
}