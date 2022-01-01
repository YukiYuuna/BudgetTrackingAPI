package com.rigel.ExpenseTracker.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rigel.ExpenseTracker.exception.BadRequestException;
import com.rigel.ExpenseTracker.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

import static javax.persistence.CascadeType.*;
import static javax.persistence.CascadeType.DETACH;

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

        if(this.expenseCategories != null)
            this.expenseCategories.add(expenseCategory);
        else{
            Set<ExpenseCategory> category = new HashSet<>();
            category.add(expenseCategory);
            this.expenseCategories = category;
        }
    }

    public int numberOfTransactions(String categoryName){
        if(this.expenseCategories == null)
            throw new BadRequestException("User has no assigned categories.");

        Optional<ExpenseCategory> expenseCategory = this.expenseCategories.stream().filter(c -> c.getCategoryName().equals(categoryName)).findFirst();
        if(expenseCategory.isEmpty())
            throw new NotFoundException("Category with this name doesn't exist in the DB.");
        else{
            if(expenseCategory.get().getExpenseTransactions() == null)
                return  0;
            return expenseCategory.get().getExpenseTransactions().size();
        }
    }

    public void addExpenseAmountToUser(ExpenseTransaction expenseTransaction){
        this.currentBudget -= expenseTransaction.getExpenseAmount();
    }
}
