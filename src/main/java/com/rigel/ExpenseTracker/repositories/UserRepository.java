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

    @Query("SELECT u "
            + "FROM User u "
            + "WHERE "
            + "u.userId = ?1")
    User fetchUserById(Long id);

    User findByUsername(String username);

    @Query("SELECT u "
            + "FROM User u "
            + "WHERE "
            + "lower(u.username) "
            + "LIKE :#{#username == null || #username.isEmpty()? '%' : #username + '%'} ")
    Page<User> filterUsers(Pageable pageable, String username);

    Optional<User> findUserByUsername(String username);

    boolean existsById(Long id);

    boolean existsByUsername(String username);

    void deleteUserByUsername(String username);
}
