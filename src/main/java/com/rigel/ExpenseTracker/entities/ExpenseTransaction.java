package com.rigel.ExpenseTracker.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;


@Entity
@Table(name = "expense_transaction")
@Data
public class ExpenseTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "expense_amount")
    private Double expenseAmount;

    @Column(name = "category_name")
    private String category;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
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
}
