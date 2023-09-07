package com.expense.service;

import java.time.LocalDate;

public interface ExpenseService {

    void generateExpenseReport(LocalDate initialDate, LocalDate endDate);
}
