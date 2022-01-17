package com.rigel.ExpenseTracker.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

import static javax.persistence.CascadeType.*;
import static javax.persistence.CascadeType.DETACH;

@Entity
@Table(name = "income_transaction")
@Getter
@Setter
public class IncomeTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "income_transaction_id", insertable = false, updatable = false)
    private Long incomeTransactionId;

    @NotNull
    @Column(name = "date")
    private LocalDate date;

    @Column(name = "income_amount")
    private Double incomeAmount;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {PERSIST, MERGE, REFRESH, DETACH})
    @JoinColumn(name = "income_category_transaction_id", referencedColumnName = "income_category_id")
    @JsonIgnore
    private IncomeCategory incomeCategory;

    @ManyToOne(cascade=ALL)
    @JoinColumn(name = "user_income_transaction_id", referencedColumnName = "user_id")
    @JsonIgnore
    private User user;

    public IncomeTransaction() {
    }

    public IncomeTransaction(LocalDate date, Double incomeAmount, String categoryName, String description, User user) {
        this.date = date;
        this.incomeAmount = incomeAmount;
        this.categoryName = categoryName;
        this.description = description;
        this.user = user;
    }
}
