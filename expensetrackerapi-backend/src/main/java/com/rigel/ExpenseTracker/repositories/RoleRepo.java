package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role, Long> {
    Role findByRoleName(String roleName);
    boolean existsByRoleName(String roleName);
}
