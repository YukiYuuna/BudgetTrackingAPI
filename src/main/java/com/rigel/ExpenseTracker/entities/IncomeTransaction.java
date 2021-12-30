package com.rigel.ExpenseTracker.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

import static javax.persistence.CascadeType.*;
import static javax.persistence.CascadeType.DETACH;

@Entity
@Table(name = "income_transaction")
@Data
public class IncomeTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "income_transaction_id")
    private Long incomeTransactionId;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "income_amount")
    private Double incomeAmount;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {PERSIST, MERGE, REFRESH, DETACH})
    @JoinColumn(name = "income_category_transaction_id", referencedColumnName = "income_category_id")
    @JsonIgnore
    private IncomeCategory incomeCategory;

    @ManyToOne(fetch = FetchType.EAGER, cascade=ALL)
    @JoinColumn(name = "user_income_transaction_id", referencedColumnName = "user_id")
    @JsonIgnore
    private User user;

    public IncomeTransaction() {
    }

    public IncomeTransaction(LocalDate date, Double incomeAmount, IncomeCategory incomeCategory, String description) {
        this.date = date;
        this.incomeAmount = incomeAmount;
        this.incomeCategory = incomeCategory;
        this.description = description;
    }

}
