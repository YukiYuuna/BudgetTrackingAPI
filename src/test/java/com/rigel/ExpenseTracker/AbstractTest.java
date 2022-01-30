package com.rigel.ExpenseTracker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rigel.ExpenseTracker.entities.Role;
import com.rigel.ExpenseTracker.entities.User;
import org.springframework.boot.json.JsonParseException;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class AbstractTest {

    protected User getNormalUser(){
        User user = new User(999L, "koko", "koko", "Koko", "Bor", "kbor@gmail.com", 10000.0);
        user.setRoles(Set.of(new Role(Role.USER)));
        return user;
    }

    protected User getAdminUser(){
        User user = new User(111L, "ivan", "ivan", "Ivan", "Duhov", "iduhov@gmail.com", 50696.0);
        user.setRoles(Set.of(new Role(Role.ADMIN)));
        return user;
    }

    protected List<User> setOfUsers(){
        User user1 = new User("petar", "petar", "Petar", "Petar", "ppet@gmail.com", 100000.0);
        User user2 = new User("deni", "deni", "Deni", "Duhova", "deniduhova@gmail.com", 8000.0);
        User user3 = new User("john", "john", "John", "Murphy", "jmurphy@gmail.com", 605.8);
        User user4 = new User("desi", "desi", "Desi", "Popova", "desippv@gmail.com", 8151125.0);
        List<User> users = List.of(user1,user2,user3, user4);
        for (User user : users) {
            user.setRoles(Set.of(new Role(Role.USER)));
        }
        return users;
    }

    protected String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
    protected <T> T mapFromJson(String json, Class<T> clazz)
            throws JsonParseException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, clazz);
    }
}
