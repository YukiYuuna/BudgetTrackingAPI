package com.rigel.ExpenseTracker.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "expense_category")
@AllArgsConstructor
@Getter
@Setter
public class ExpenseCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_category_id")
    @JsonIgnore
    private Long expenseCategoryId;

    @Column(name = "category_name")
    private String categoryName;

    @JsonIgnore
    @OneToMany(mappedBy = "expenseCategory")
    private List<ExpenseTransaction> expenseTransactions;

    @ManyToOne(cascade = ALL)
    @JoinColumn(name = "user_category_id", referencedColumnName = "user_id")
    @JsonIgnore
    private User user;

    public ExpenseCategory() {
    }

    public ExpenseCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpenseCategory category = (ExpenseCategory) o;
        return Objects.equals(expenseCategoryId, category.expenseCategoryId) && Objects.equals(categoryName, category.categoryName) && Objects.equals(expenseTransactions, category.expenseTransactions) && Objects.equals(user, category.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expenseCategoryId, categoryName, expenseTransactions, user);
    }
}
