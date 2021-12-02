package com.rigel.ExpenseTracker.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "expense_category")
public class ExpenseCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_name")
    private String categoryName;

    @OneToMany(mappedBy = "expenseCategory", cascade = CascadeType.ALL)
    private Set<ExpenseTransaction> expenseTransactions;

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
}
