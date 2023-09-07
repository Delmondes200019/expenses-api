package com.expense.service.email;

import com.expense.dto.ExpenseReportInfo;
import com.expense.model.Expense;

import java.time.LocalDate;
import java.util.List;

public interface ExpensesReportEmailService {

    void sendReport(ExpenseReportInfo expenseReportInfo);
}
