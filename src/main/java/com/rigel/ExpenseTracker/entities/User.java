package com.rigel.ExpenseTracker.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "budget")
    private Double currentBudget;

//    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<ExpenseCategory> expenseCategories;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<ExpenseTransaction> expenseTransactions;

    public User() {
    }

    public User(String username, String firstName, String lastName, String email, Double currentBudget) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.currentBudget = currentBudget;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getCurrentBudget() {
        return currentBudget;
    }

    public void setCurrentBudget(Double currentBudget) {
        this.currentBudget = currentBudget;
    }

    public Set<ExpenseCategory> getExpenseCategories() {
        return expenseCategories;
    }

    public void setExpenseCategories(Set<ExpenseCategory> expenseCategories) {
        this.expenseCategories = expenseCategories;
    }
}
