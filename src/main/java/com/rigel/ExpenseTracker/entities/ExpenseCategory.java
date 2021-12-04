package com.rigel.ExpenseTracker.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "expense_category")
@Data
public class ExpenseCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_name")
    private String categoryName;

    @OneToMany(mappedBy = "expenseCategory", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ExpenseTransaction> expenseTransactions;

    public ExpenseCategory() {
    }

    public ExpenseCategory(String categoryName) {
        this.categoryName = categoryName;
    }
}
