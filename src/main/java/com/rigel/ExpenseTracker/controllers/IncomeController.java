package com.rigel.ExpenseTracker.controllers;


import com.rigel.ExpenseTracker.repositories.IncomeCategoryRepository;
import com.rigel.ExpenseTracker.repositories.IncomeTransactionRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/income")
@CrossOrigin(origins = "*")
public class IncomeController {

    private final IncomeCategoryRepository incomeCategoryRepository;
    private final IncomeTransactionRepository incomeTransactionRepository;

    public IncomeController(IncomeCategoryRepository incomeCategoryRepository, IncomeTransactionRepository incomeTransactionRepository) {
        this.incomeCategoryRepository = incomeCategoryRepository;
        this.incomeTransactionRepository = incomeTransactionRepository;
    }
}
