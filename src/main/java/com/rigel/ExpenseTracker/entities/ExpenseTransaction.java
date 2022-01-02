package com.rigel.ExpenseTracker.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "expense_transaction")
@Getter
@Setter
public class ExpenseTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_transaction_id", insertable = false, updatable = false)
    private Long expenseTransactionId;

    @NotNull
    @Column(name = "date")
    private LocalDate date;

    @Column(name = "expense_amount")
    private Double expenseAmount;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY,  cascade = {PERSIST, MERGE, REFRESH, DETACH})
    @JoinColumn(name = "expense_category_transaction_id", referencedColumnName = "expense_category_id")
    @JsonIgnore
    private ExpenseCategory expenseCategory;

    @ManyToOne(cascade = ALL)
    @JoinColumn(name = "user_expense_transaction_id", referencedColumnName = "user_id")
    @JsonIgnore
    private User user;

    public ExpenseTransaction() {
    }

    public ExpenseTransaction(LocalDate date, Double expenseAmount, String categoryName, String description) {
        this.date = date;
        this.expenseAmount = expenseAmount;
        this.categoryName = categoryName;
        this.description = description;
    }

    public ExpenseTransaction(LocalDate date, Double expenseAmount, String categoryName, String description, User user) {
        this.date = date;
        this.expenseAmount = expenseAmount;
        this.categoryName = categoryName;
        this.description = description;
        this.user = user;
    }
}
