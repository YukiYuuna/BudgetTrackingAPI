package com.rigel.ExpenseTracker.repositories;

import com.rigel.ExpenseTracker.entities.ExpenseCategory;
import com.rigel.ExpenseTracker.entities.ExpenseTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.StyledEditorKit;
import java.util.List;
import java.util.Set;

public interface ExpenseCategoryRepository  extends JpaRepository<ExpenseCategory, Long> {

    @Query("SELECT c FROM ExpenseCategory c")
    Set<ExpenseCategory> findAllCategories();

    ExpenseCategory findExpenseCategoryById(Long id);

    Boolean existsExpenseCategoryByCategoryName(String name);

    ExpenseCategory findExpenseCategoryByCategoryName(String name);

}
