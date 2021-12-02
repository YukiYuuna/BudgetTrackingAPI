package com.rigel.ExpenseTracker.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "income_category")
public class IncomeCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "category_name")
    private String categoryName;

    @JsonIgnore
    @OneToMany(mappedBy = "incomeCategory")
    private List<IncomeTransaction> incomeTransaction;

    public IncomeCategory() {
    }

    public IncomeCategory(String categoryName){
        this.categoryName = categoryName;
    }

    public Long getId() {
        return Id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<IncomeTransaction> getIncomeTransaction() {
        return incomeTransaction;
    }

    public void setIncomeTransaction(List<IncomeTransaction> incomeTransaction) {
        this.incomeTransaction = incomeTransaction;
    }
}