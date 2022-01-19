package com.rigel.ExpenseTracker.controllers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;
    @Autowired
    WebApplicationContext webApplicationContext;
    @Mock private UserRepository mockUserRepo;
    @Mock private RoleRepo mockRoleRepo;
    @Mock private PasswordEncoder mockPasswordEncoder;
    @Mock private UserService userServiceTest;

    protected void setMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    protected String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }

    protected <T> T mapFromJson(String json, Class<T> clazz)
            throws JsonParseException, JsonMappingException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, clazz);
    }

    @BeforeEach
    void setUp() {
//        autoCloseable = MockitoAnnotations.openMocks(this);
        userServiceTest = new UserServiceImpl(mockUserRepo, mockRoleRepo, mockPasswordEncoder);
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

    @BeforeEach
    void init() {
        User admin = new User("admin", "admin", "Koko", "Borimechkov", "koko@gmail.com", 9000.0);
        User user1 = new User("ivan", "ivan", "Ivan", "Duhov", "ivanDuhov@gmail.com", 100000.0);
        User user2 = new User("ivo", "ivo", "Ivo", "Petkov", "ipetkov@gmail.com", 800.0);
        mockUserRepo.save(admin);
        mockUserRepo.save(user1);
        mockUserRepo.save(user2);
    }

    @AfterEach
    void tearDown() {
        mockUserRepo.deleteAll();
    }

    @Test
    void getUserInfo() {
        verify(userServiceTest).getUser();

    }

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