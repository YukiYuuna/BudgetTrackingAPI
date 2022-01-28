package com.rigel.ExpenseTracker.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.*;
import java.util.*;

import static javax.persistence.CascadeType.*;
import static javax.persistence.CascadeType.DETACH;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Entity
@Table(name="users")
@AllArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @JsonIgnore
    private Long userId;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "budget")
    private Double currentBudget;

    @ManyToMany(fetch = FetchType.EAGER, cascade =  {PERSIST, MERGE, REFRESH, DETACH})
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = { @JoinColumn(name = "role_id")})
    @JsonIgnore
    private Set<Role> roles;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<ExpenseCategory> expenseCategories;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<ExpenseTransaction> expenseTransactions;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<IncomeCategory> incomeCategories;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<IncomeTransaction> incomeTransactions;

    public User() {
    }

    public User(Long userId, String username, String password, String firstName, String lastName, String email, Double currentBudget) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.currentBudget = currentBudget;
    }

    public User(String username, String password, String firstName, String lastName, String email, Double currentBudget) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.currentBudget = currentBudget;
    }

    public void addRoleToUser(Role role){
        Set<Role> newRoles = new HashSet<>();
        if(this.roles != null) {
            newRoles.add(role);
            newRoles.addAll(this.roles);
        }
        else
            newRoles.add(role);

        setRoles(newRoles);
    }

    public void addExpenseCategoryToUser(ExpenseCategory expenseCategory){
        Set<ExpenseCategory> newCategories = new HashSet<>();
        if(this.expenseCategories != null){
            newCategories.add(expenseCategory);
            newCategories.addAll(this.expenseCategories);
            }
        else
            newCategories.add(expenseCategory);

        setExpenseCategories(newCategories);
    }

    public void addIncomeCategoryToUser(IncomeCategory incomeCategory){
        Set<IncomeCategory> newCategories = new HashSet<>();
        if(this.incomeCategories != null){
            newCategories.add(incomeCategory);
            newCategories.addAll(this.incomeCategories);
        }
        else
            newCategories.add(incomeCategory);

        setIncomeCategories(newCategories);
    }

    public void addExpenseAmountToUser(Double expenseAmount){
        this.currentBudget -= expenseAmount;
    }

    public void addIncomeAmountToUser(Double incomeAmount){
        this.currentBudget += incomeAmount;
    }
}
