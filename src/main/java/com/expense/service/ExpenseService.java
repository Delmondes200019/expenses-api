package com.expense.service;

import com.expense.dto.ExpenseView;
import com.expense.dto.NewExpense;

import java.time.LocalDate;

public interface ExpenseService {

    void generateExpenseReport(LocalDate initialDate, LocalDate endDate);

    ExpenseView generateExpense(NewExpense newExpense);
}
