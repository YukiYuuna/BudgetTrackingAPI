package com.rigel.ExpenseTracker.controllers;


import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.IncomeTransaction;
import com.rigel.ExpenseTracker.repositories.IncomeCategoryRepository;
import com.rigel.ExpenseTracker.repositories.IncomeTransactionRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/income")
@CrossOrigin(origins = "*")
public class IncomeController {

    private final IncomeCategoryRepository incomeCategoryRepository;
    private final IncomeTransactionRepository incomeTransactionRepository;

    public IncomeController(IncomeCategoryRepository incomeCategoryRepository, IncomeTransactionRepository incomeTransactionRepository) {
        this.incomeCategoryRepository = incomeCategoryRepository;
        this.incomeTransactionRepository = incomeTransactionRepository;
    }

    @GetMapping("/transactions")
    public List<IncomeTransaction> fetchAllIncomeTransactions() {
        return incomeTransactionRepository.findAll();
    }

}
