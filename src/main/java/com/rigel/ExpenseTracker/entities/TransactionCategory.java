package com.rigel.ExpenseTracker.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Data
public class TransactionCategory {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "category_type")
    private String categoryType;
}
