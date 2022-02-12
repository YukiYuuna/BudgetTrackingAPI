package com.rigel.ExpenseTracker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.Role;
import com.rigel.ExpenseTracker.entities.User;
import org.springframework.boot.json.JsonParseException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
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

    protected List<ExpenseTransaction> listOfTransactions(User user, User secondUser){
        ExpenseCategory food = new ExpenseCategory(1L, "food", user);
        ExpenseCategory housing = new ExpenseCategory(2L,"housing", user);
        ExpenseCategory gifts = new ExpenseCategory(3L, "gifts", user);
        ExpenseCategory subscription = new ExpenseCategory(4L, "subscription", user);

        ExpenseTransaction first = new ExpenseTransaction(1L, LocalDate.parse("2021-12-31"), 90.0, "food",
                "Bought some groceries", user, food);
        ExpenseTransaction second = new ExpenseTransaction(2L, LocalDate.parse("2021-12-31"), 800.0, "housing",
                "Monthly rent payment.", user, food);
        ExpenseTransaction third = new ExpenseTransaction(3L, LocalDate.parse("2022-09-02"), 50.0, "subscriptions",
                "Yearly Netflix.", secondUser, subscription);
        ExpenseTransaction fourth = new ExpenseTransaction(4L, LocalDate.parse("2022-01-01"), 300.50, "gifts",
                "Gifts for friends", user, gifts);
        ExpenseTransaction fifth = new ExpenseTransaction(5L, LocalDate.parse("2021-12-01"), 60.0, "food",
                "Bought some groceries", user, food);
        ExpenseTransaction sixth = new ExpenseTransaction(6L, LocalDate.parse("2021-11-01"), 300.0, "food",
                "Monthly groceries.", secondUser, food);
        ExpenseTransaction seventh = new ExpenseTransaction(7L, LocalDate.parse("2021-09-25"), 600.0, "housing",
                "Monthly rent payment.", secondUser, housing);
        ExpenseTransaction eight = new ExpenseTransaction(8L, LocalDate.parse("2021-06-25"), 20.0, "subscription",
                "Amazon Delivery", secondUser, subscription);

        List<ExpenseTransaction> transactions = new ArrayList<>(List.of(first, second, fourth,fifth));
        List<ExpenseTransaction> secondUserTransactions = List.of(third, sixth, seventh,eight);

        user.setExpenseTransactions(transactions);
        user.setExpenseCategories(Set.of(food, housing, gifts));
        secondUser.setExpenseTransactions(secondUserTransactions);
        secondUser.setExpenseCategories(Set.of(food, housing, subscription));

        List<ExpenseTransaction> result = new ArrayList<>();
        result.addAll(transactions);
        result.addAll(secondUserTransactions);
        return result;
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
