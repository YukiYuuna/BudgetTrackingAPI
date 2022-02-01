package com.rigel.ExpenseTracker.controllers;

import com.rigel.ExpenseTracker.AbstractTest;
import com.rigel.ExpenseTracker.entities.Role;
import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(SecurityController.class)
class SecurityControllerTest extends AbstractTest {
    @MockBean UserServiceImpl service;
    @MockBean BCryptPasswordEncoder encoder;

    private MockMvc mockMvc;
    @Autowired WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .defaultRequest(get("/api").with(user("koko").password("koko").roles(Role.USER, Role.ADMIN)))
                .apply(springSecurity())
                .build();
    }

    @Test
    void successfulUserRegistration() throws Exception{
//        given
        User user = new User(999L, "koko", "koko", "Konstantin", "Borimechkov", "kborimechkov@gmail.com", 5000.0);
        user.setRoles(Set.of(new Role(Role.USER)));
        given(service.getAllDBUsers()).willReturn(setOfUsers());

//        when
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(user))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
//        then
        then(service).should(times(1)).saveUser(any());
        assertThat(response.getResponse().getContentAsString()).isEqualTo(user.getFirstName() + " " + user.getLastName()
                + " has been added successfully!");

    }

    @Test
    void invalidUsernameRegistration() throws Exception{
//        given
        User user = new User(999L, "desi", "desi", "Desislava", "Popova", "desippv@gmail.com", 500050.5);
        user.setRoles(Set.of(new Role(Role.USER)));
        given(service.getAllDBUsers()).willReturn(setOfUsers());

//        when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(user))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
//        then
        verify(service,never()).saveUser(any());
    }

    @Test
    void invalidEmailRegistration() throws Exception{
//        given
        User user = new User(999L, "koko", "koko", "Konstantin", "Borimechkov", "deniduhova@gmail.com", 5000.0);
        user.setRoles(Set.of(new Role(Role.USER)));
        given(service.getAllDBUsers()).willReturn(setOfUsers());

//        when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(user))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
//        then
        verify(service,never()).saveUser(any());
    }

    @Test
    void notEnoughDataProvided_ForRegistration() throws Exception {
//        given
//        Use doesn't provide last name in this example:
        User user = new User(999L, "koko", "koko", "Konstantin", null, "deniduhova@gmail.com", 5000.0);
        user.setRoles(Set.of(new Role(Role.USER)));
        given(service.getAllDBUsers()).willReturn(setOfUsers());

//        when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(user))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
//        then
        verify(service, never()).saveUser(any());
        String providedException = mapFromJson(result.getResponse().getContentAsString(), LinkedHashMap.class).get("message").toString();
        assertThat(providedException).isEqualTo("Make sure you provide all data, including: username, password, first and last name, current budget!");
    }
}