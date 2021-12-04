package com.rigel.ExpenseTracker.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "income_transaction")
@Data
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

}
