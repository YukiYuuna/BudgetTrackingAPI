package com.rigel.ExpenseTracker.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "income_category")
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

    @Column(name = "color")
    @Nullable
    private String color;

    @JsonIgnore
    @OneToMany(mappedBy = "incomeCategory")
    private List<IncomeTransaction> incomeTransactions;

    @ManyToOne(cascade = {PERSIST, MERGE, REFRESH, DETACH})
    @JoinColumn(name = "user_income_category_id", referencedColumnName = "user_id")
    @JsonIgnore
    private User user;

    public IncomeCategory() {
    }

    public IncomeCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    public IncomeCategory(String categoryName, User user) {
        this.categoryName = categoryName;
        this.user = user;
    }

    public IncomeCategory(String categoryName, @Nullable String color, User user) {
        this.categoryName = categoryName;
        this.user = user;
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IncomeCategory that = (IncomeCategory) o;
        return Objects.equals(incomeCategoryId, that.incomeCategoryId) && Objects.equals(categoryName, that.categoryName) && Objects.equals(incomeTransactions, that.incomeTransactions) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(incomeCategoryId, categoryName, incomeTransactions, user);
    }
}