package com.rigel.ExpenseTracker.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;

@Entity
@Table(name = "expense_category")
public class ExpenseCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(name = "category_name")
    private String categoryName;

    @OneToMany(mappedBy = "expenseCategory", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ExpenseTransaction> expenseTransactions;

    @ManyToOne(cascade = ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;

    public ExpenseCategory() {
    }

    public ExpenseCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getId() {
        return id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Set<ExpenseTransaction> getExpenseTransactions() {
        return expenseTransactions;
    }

    public void setExpenseTransactions(Set<ExpenseTransaction> expenseTransactions) {
        this.expenseTransactions = expenseTransactions;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
