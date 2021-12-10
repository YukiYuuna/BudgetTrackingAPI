package com.rigel.ExpenseTracker.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;


@Entity
@Table(name = "expense_transaction")
public class ExpenseTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "date")
    private LocalDate date;

    @Column(name = "expense_amount")
    private Double expenseAmount;

    @Column(name = "category_name")
    private String category;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "expense_category_id", referencedColumnName = "Id")
    @JsonIgnore
    private ExpenseCategory expenseCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "Id")
    @JsonIgnore
    @NotNull
    private User user;

    public ExpenseTransaction() {
    }

    public ExpenseTransaction(LocalDate date, Double expenseAmount, String categoryName, ExpenseCategory expenseCategory, String description) {
        this.date = date;
        this.expenseAmount = expenseAmount;
        this.category = categoryName;
        this.expenseCategory = expenseCategory;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getExpenseAmount() {
        return expenseAmount;
    }

    public void setExpenseAmount(Double expenseAmount) {
        this.expenseAmount = expenseAmount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ExpenseCategory getExpenseCategory() {
        return expenseCategory;
    }

    public void setExpenseCategory(ExpenseCategory expenseCategory) {
        this.expenseCategory = expenseCategory;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
