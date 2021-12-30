package com.rigel.ExpenseTracker.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

import static javax.persistence.CascadeType.*;
import static javax.persistence.CascadeType.DETACH;

@Entity
@Table(name = "income_category")
@Data
@Getter
@Setter
@AllArgsConstructor
public class IncomeCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "income_category_id")
    private Long incomeCategoryId;

    @Column(name = "category_name")
    private String categoryName;

    @JsonIgnore
    @OneToMany(mappedBy = "incomeCategory")
    private List<IncomeTransaction> incomeTransactions;

    @ManyToOne(fetch = FetchType.EAGER, cascade = ALL)
    @JoinColumn(name = "user_income_category_id", referencedColumnName = "user_id")
    @JsonIgnore
    private User user;

    public IncomeCategory() {
    }

    public IncomeCategory(String categoryName) {
        this.categoryName = categoryName;
    }
}