package com.rigel.ExpenseTracker.entities;

import lombok.Data;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Data
public class TransactionCategory {
    @Column(name = "user_id")
    private Long userId;
}
