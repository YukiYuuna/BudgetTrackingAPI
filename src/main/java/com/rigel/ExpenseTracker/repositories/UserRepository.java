package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserById(Long id);

    User findUserByFirstNameAndLastName(String fName, String lName);

    boolean existsById(Long id);

    User findUserByExpenseTransactions(ExpenseTransaction expenseTransaction);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE User u SET u.currentBudget = ?2 WHERE u.id = ?1")
    User setCurrentBudget(Long id, Double modifiedBudget);

}
