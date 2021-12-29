package com.rigel.ExpenseTracker.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rigel.ExpenseTracker.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name="users")
@AllArgsConstructor
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username")
    private String username;

    @JsonIgnore
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = { @JoinColumn(name = "role_id")})
    private Set<Role> roles;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<ExpenseCategory> expenseCategories;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<ExpenseTransaction> expenseTransactions;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<IncomeCategory> incomeCategories;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<IncomeTransaction> incomeTransactions;

    public User() {
    }

    public User(String username, String password, String firstName, String lastName, String email, Double currentBudget) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.currentBudget = currentBudget;
    }

    public void addExpenseCategoryToUser(ExpenseCategory expenseCategory){
        if(expenseCategories.stream()
                .anyMatch(category -> category.getCategoryName().equals(expenseCategory.getCategoryName())))
            throw new BadRequestException("Category already exists.");

        this.expenseCategories.add(expenseCategory);
    }

    public void addExpenseAmountToUser(ExpenseTransaction expenseTransaction){
        this.currentBudget -= expenseTransaction.getExpenseAmount();
    }

    public void removeCategoryFromUser(String categoryName){
        this.expenseCategories = this.getExpenseCategories().stream()
                .filter(category -> !(category.getCategoryName().equals(categoryName)))
                .collect(Collectors.toSet());
    }
}
