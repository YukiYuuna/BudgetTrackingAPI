package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserById(Long id);

    User findUserByFirstNameAndLastName(String firstName, String lastName);

    @Query("SELECT u "
            + "FROM User u "
            + "WHERE "
            + "lower(u.firstName) "
            + "LIKE :#{#firstName == null || #firstName.isEmpty()? '%' : #firstName + '%'} "
            + "AND lower(u.lastName) "
            + "LIKE :#{#lastName == null || #lastName.isEmpty()? '%' : #lastName + '%'}")
    Page<User> filterUsers(Pageable pageable, String firstName, String lastName);

    boolean existsById(Long id);

    boolean existsByFirstNameAndLastName(String fName, String lName);

    User findUserByExpenseTransactions(ExpenseTransaction expenseTransaction);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE User u SET u.currentBudget = ?2 WHERE u.id = ?1")
    User setCurrentBudget(Long id, Double modifiedBudget);


}
