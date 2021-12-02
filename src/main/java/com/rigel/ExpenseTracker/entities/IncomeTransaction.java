package com.rigel.ExpenseTracker.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "income_transaction")
public class IncomeTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "income_amount")
    private Double incomeAmount;

    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "income_category_id", referencedColumnName = "Id")
    private IncomeCategory incomeCategory;

    @JsonIgnore
    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "users_id", referencedColumnName = "Id")
    private User users;

    public IncomeTransaction() {
    }

    public IncomeTransaction(LocalDate date, Double incomeAmount, IncomeCategory incomeCategory) {
        this.date = date;
        this.incomeAmount = incomeAmount;
        this.incomeCategory = incomeCategory;
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

    public Double getIncomeAmount() {
        return incomeAmount;
    }

    public void setIncomeAmount(Double incomeAmount) {
        this.incomeAmount = incomeAmount;
    }

    public IncomeCategory getIncomeCategory() {
        return incomeCategory;
    }

    public void setIncomeCategory(IncomeCategory incomeCategory) {
        this.incomeCategory = incomeCategory;
    }

    public User getUsers() {
        return users;
    }

    public void setUsers(User users) {
        this.users = users;
    }

}
