package com.rigel.ExpenseTracker.service;

import com.rigel.ExpenseTracker.entities.Role;
import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.repositories.RoleRepo;
import com.rigel.ExpenseTracker.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;


@Slf4j
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository mockUserRepo;
    @Mock private RoleRepo mockRoleRepo;
    @Mock private PasswordEncoder mockPasswordEncoder;
    @InjectMocks private UserServiceImpl userService;

    final Long id = 1L;

    @BeforeAll
    public static void beforeAll() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getDetails()).thenReturn(
                new User("admin", "admin", "Koko", "Borimechkov", "koko@gmail.com", 9000.0));
    }

    @Test
    void savingUserToDB() {
//        given
        User user = getOneUser();
        when(mockPasswordEncoder.encode(user.getPassword())).thenReturn("encoded");
        Role role = new Role(Role.ROLE_USER);
        when(mockRoleRepo.findByRoleName(role.getRoleName())).thenReturn(role);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

//        when
        userService.saveUser(user);

//        then
        then(mockUserRepo).should(atMost(2)).save(userArgumentCaptor.capture());
        assertThat(userArgumentCaptor).isNotNull();
        User capturedUser = userArgumentCaptor.getValue();
        assertThat(capturedUser).isEqualTo(user);
    }

    @Test
    void savingRoleToDB() {
//        given
        Role role = new Role(Role.ROLE_ADMIN);
        role.setRoleId(1);
        when((mockRoleRepo.existsByRoleName(role.getRoleName()))).thenReturn(false);
        ArgumentCaptor<Role> roleArgumentCaptor = ArgumentCaptor.forClass(Role.class);

//        when
        userService.saveRole(role);

//        then
        then(mockRoleRepo).should(atMost(2)).save(roleArgumentCaptor.capture());
        assertThat(roleArgumentCaptor).isNotNull();
        Role capturedRole = roleArgumentCaptor.getValue();
        assertThat(capturedRole).isEqualTo(role);
    }

    @Test
    void notAbleToSaveRoleToDB() {
//        given
        Role role = new Role(Role.ROLE_ADMIN);
        role.setRoleId(1);
        when(mockRoleRepo.existsByRoleName(role.getRoleName())).thenReturn(true);

//        when
//        then
        assertThatThrownBy(() -> userService.saveRole(role)).isInstanceOf(ResponseStatusException.class)
                .hasMessage("400 BAD_REQUEST \"This role already exists in the DB!\"");

        verify(mockUserRepo, never()).save(any());
    }

    @Test
    void addingRoleToUser() {
//        given
        User user = getOneUser();
        Role role = new Role("ROLE_SUPER_USER");
        when(mockRoleRepo.findByRoleName(role.getRoleName())).thenReturn(role);
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(user.getUsername());
        when(mockUserRepo.findUserByUsername(user.getUsername())).thenReturn(Optional.of(user));

//        when
        userService.addRoleToUser(role.getRoleName());

//        then
        assertThat(userService.getUser().getRoles()).isEqualTo(user.getRoles());
        verify(mockRoleRepo, atMost(1)).save(role);
    }

    @Test
    void checkingNumberOfUsersInDB() {
//        given
//        when
//        then
        when(mockUserRepo.findAll()).thenReturn(setOfUsers());
        assertThat(userService.numberOfUsers()).isEqualTo(4);
    }

    @Test
    void gettingUserAsOptional() {
//        given
        User user = getOneUser();
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(user.getUsername());
        lenient().when(mockUserRepo.findUserByUsername(user.getUsername())).thenReturn(Optional.of(user));

//        when
        Optional<User> userOptional = userService.getOptionalUser();

//        then
        assertThat(userOptional).isNotNull();
        assertThat(userOptional).isEqualTo(Optional.of(user));
    }

    @Test
    void notAbleToGetUserAsOptional() {
//        given
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("ivan");

//        when
//        then
        assertThatThrownBy(() -> userService.getOptionalUser()).isInstanceOf(ResponseStatusException.class)
                .hasMessage("400 BAD_REQUEST \"Sorry, something went wrong.\"");
    }

    @Test
    void gettingUserById() {
//        given
        User user1 =getOneUser();
        user1.setUserId(id);
        when(mockUserRepo.findUserByUserId(id)).thenReturn(Optional.of(user1));

//        when
        User userResult = userService.getUserById(id);

//        then
        then(mockUserRepo).should(atMost(1)).getById(id);
        assertThat(userResult).isNotNull();
        assertThat(userResult).isEqualTo(user1);
    }

    @Test
    void notAbleToGetUserById() {
//        given
        given(mockUserRepo.findUserByUserId(id)).willReturn(Optional.empty());

//        when
//        then
        assertThatThrownBy(() -> userService.getUserById(1L)).isInstanceOf(ResponseStatusException.class)
                .hasMessage("404 NOT_FOUND \"No user with id 1 found in the DB!\"");
        verify(mockUserRepo, never()).getById(any());
    }


    @Test
    void gettingAllDBUsers() {
//        when
        userService.getAllDBUsers();

//        then
        /*
         *       basically we want to say that the userRepo mock was invoked using the method findAll(),
         *       when using the getAllDBUsers in the serviceImpl
         */
        then(mockUserRepo).should(atMost(1)).findAll();
    }

    @Test
    void gettingAllUsersAsPage() {
//        given
        Pageable pageable = PageRequest.of(1, 2);
        List<User> users = setOfUsers();
        Page<User> page = new PageImpl<>(users, pageable, users.size());
        when(mockUserRepo.filterUsers(pageable)).thenReturn(page);

//        when
//        then
        assertThat(userService.getUsers(pageable)).isEqualTo(page);
    }

    @Test
    void checkingIfUsernameExistsInDB() {
//        given
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("ivan");

//        when
        userService.usernameExists();

//        then
        verify(mockUserRepo).existsByUsername("ivan");
    }

    private static List<User> setOfUsers(){
        User user1 = new User("ivan", "ivan", "Ivan", "Duhov", "ivanDuhov@gmail.com", 100000.0);
        User user2 = new User("deni", "deni", "Deni", "Duhova", "deniduhova@gmail.com", 9794.0);
        User user3 = new User("koko", "koko", "Koko", "Bor", "kbor@gmail.com", 4643.0);
        User user4 = new User("desi", "desi", "Desi", "Popova", "desippv@gmail.com", 8151125.0);
        return List.of(user1,user2,user3, user4);
    }

    private User getOneUser(){
        User user = new User(999L, "koko", "koko", "Koko", "Bor", "kbor@gmail.com", 10000.0);
        user.setRoles(Set.of(new Role(Role.ROLE_USER)));
        mockUserRepo.save(user);
        return user;
    }
}