package com.rigel.ExpenseTracker.controllers;

import com.rigel.ExpenseTracker.AbstractTest;
import com.rigel.ExpenseTracker.entities.Role;
import com.rigel.ExpenseTracker.entities.User;
import com.rigel.ExpenseTracker.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(UserController.class)
class UserControllerTest extends AbstractTest {
    @MockBean UserServiceImpl service;
    @MockBean BCryptPasswordEncoder encoder; //needed for configuration of API security

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
    void gettingUserInformation() throws Exception {
//        given
        User user = getNormalUser();
        given(service.getUser()).willReturn(user);

//        when
        MvcResult result = mockMvc.perform(get("/api/user")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk()).andReturn();

//        then
        User providedUser = mapFromJson(result.getResponse().getContentAsString(), User.class);
        assertThat(providedUser).isNotNull();
        assertThat(user.getUsername()).isEqualTo(providedUser.getUsername());
        assertThat(user.getEmail()).isEqualTo(providedUser.getEmail());
        assertThat(user.getCurrentBudget()).isEqualTo(providedUser.getCurrentBudget());
    }

    @Test
    void gettingUserByProvidingId() throws Exception {
//        given
        User admin = getAdminUser();
        given(service.getUserById(any())).willReturn(admin);

//        when
        MvcResult result = mockMvc.perform(get("/api/user/id"))
                        .andExpect(status().isOk()).andReturn();

//        then
        User providedUser = mapFromJson(result.getResponse().getContentAsString(), User.class);
        assertThat(providedUser).isNotNull();
        assertThat(admin.getUsername()).isEqualTo(providedUser.getUsername());
        assertThat(admin.getEmail()).isEqualTo(providedUser.getEmail());
        assertThat(admin.getCurrentBudget()).isEqualTo(providedUser.getCurrentBudget());

    }

    @Test
    void gettingAllUsersFromDB() throws Exception {
//        given
        Pageable pageable = PageRequest.of(1, 2);
        List<User> users = setOfUsers();
        Page<User> pageOfUsers = new PageImpl<>(users, pageable, users.size());
        given(service.numberOfUsers()).willReturn(users.size());
        given(service.getUsers(any())).willReturn(pageOfUsers);

//        when
        MvcResult result = mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk()).andReturn();

//        then
        ArrayList<?> providedUsers = (ArrayList<?>) mapFromJson(result.getResponse().getContentAsString(), LinkedHashMap.class).get("users");
        int index = 0;

        for (Object user: providedUsers) {
            LinkedHashMap<String, Object> u1 = (LinkedHashMap<String, Object>) user;
            assertThat(u1.get("username")).isEqualTo(users.get(index).getUsername());
            assertThat(u1.get("password")).isEqualTo(users.get(index).getPassword());
            assertThat(u1.get("email")).isEqualTo(users.get(index).getEmail());
            assertThat(u1.get("currentBudget")).isEqualTo(users.get(index).getCurrentBudget());
            index++;
        }
    }

    @Test
    void savingNewRoleInDB() throws Exception {
//        given
        Role role = new Role(Role.USER);
        ArgumentCaptor<Role> roleArgumentCaptor = ArgumentCaptor.forClass(Role.class);
        given(service.saveRole(any(Role.class))).willReturn(role);

//        when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/save/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(role))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

//        then
        Role providedRole = mapFromJson(result.getResponse().getContentAsString(), Role.class);
        then(service).should(atMost(1)).saveRole(roleArgumentCaptor.capture());
        assertThat(roleArgumentCaptor).isNotNull();
        assertThat(providedRole).isNotNull();

        Role capturedRole = roleArgumentCaptor.getValue();
        assertThat(capturedRole.getRoleName()).isEqualTo(providedRole.getRoleName());

    }

    @Test
    void addingExistingRoleToUser() throws Exception {
//        given
        Role role = new Role(Role.USER);

//        when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/add/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(role))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
//        then
        verify(service, times(1)).addRoleToUser(any());
    }

    @Test
    void modifyingUserInformation() throws Exception {
//        given
        User user = getNormalUser();
        User modUser = new User(999L, "koko", "koko", "Konstantin", "Borimechkov", "kborimechkov@gmail.com", 5000.0);

        given(service.usernameExists()).willReturn(true);
        given(service.getOptionalUser()).willReturn(Optional.of(user));

//        when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/user/modify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(modUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

//        then
        verify(service, atMost(1)).clearSave(any(User.class));
        User providedUser = mapFromJson(result.getResponse().getContentAsString(), User.class);
        assertThat(providedUser.getFirstName()).isEqualTo(modUser.getFirstName());
        assertThat(providedUser.getLastName()).isEqualTo(modUser.getLastName());
        assertThat(providedUser.getEmail()).isEqualTo(modUser.getEmail());
        assertThat(providedUser.getCurrentBudget()).isEqualTo(modUser.getCurrentBudget());
    }

    @Test
    void notAbleToModifyUserBecauseItIsNotExisting() throws Exception {
//        given
        User user = getNormalUser();
        User modUser = new User(999L, "john", "john", "john", "john", "john@gmail.com", 10.0);

        given(service.usernameExists()).willReturn(true);
        given(service.getOptionalUser()).willReturn(Optional.of(user));

//        when
        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/modify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(modUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable());

//        then
        verify(service, never()).clearSave(any());
    }

    @Test
    void notAbleToModifyUserBecauseUsernameExists() throws Exception {
//        given
        given(service.usernameExists()).willReturn(false);

//        when
        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/modify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(getNormalUser()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

//        then
        verify(service, never()).clearSave(any());
    }

    @Test
    void deletingCurrentlyLoggedInUser() throws Exception {
//        given
//        when
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/delete")
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

//        then
        then(service).should(atMost(1)).deleteUser();
        assertThat(response.getResponse().getContentAsString()).isEqualTo("User was deleted successfully!");
    }

}