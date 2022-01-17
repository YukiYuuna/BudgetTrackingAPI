package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import com.rigel.ExpenseTracker.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUserId(Long id);

    @Query("SELECT u "
            + "FROM User u")
    Page<User> filterUsers(Pageable pageable);

    Optional<User> findUserByUsername(String username);

    boolean existsById(Long id);

    boolean existsByUsername(String username);
}
