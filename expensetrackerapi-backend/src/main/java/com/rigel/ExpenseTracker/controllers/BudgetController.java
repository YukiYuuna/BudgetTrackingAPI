package com.rigel.ExpenseTracker.controllers;

import com.rigel.ExpenseTracker.service.TransactionService;
import com.rigel.ExpenseTracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BudgetController extends ControlHelper{

    private final UserService userService;
    private final TransactionService service;

    @Autowired
    public BudgetController(@Lazy UserService userService, TransactionService service){
        this.userService = userService;
        this.service = service;
    }

    @GetMapping("/user/current/budget")
    public HashMap<String, Object> fetchCurrentUserBudget(@Nullable Integer currentPage, @Nullable Integer perPage) {
        Double totalExpenseAmount = (Double) service
                .getAllUserTransactions(createPagination(currentPage, perPage, userService.numberOfUsers()),"expense")
                .get("transactionsAmount");
        Double totalIncomeAmount = (Double) service
                .getAllUserTransactions(createPagination(currentPage, perPage, userService.numberOfUsers()),"income")
                .get("transactionsAmount");
        Double userBudget = userService.getUser().getCurrentBudget();
        HashMap<String, Object> result = new HashMap<>();
        result.put("made", totalIncomeAmount);
        result.put("spent", totalExpenseAmount);
        result.put("currentBudget", (userBudget + totalIncomeAmount) - totalExpenseAmount);
        return result;
    }

}
