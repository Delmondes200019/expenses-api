package com.expense.dto;

import com.expense.model.Expense;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ExpenseReportInfo {

    private List<Expense> expenses;
    private LocalDate initialDate;
    private LocalDate endDate;
}
