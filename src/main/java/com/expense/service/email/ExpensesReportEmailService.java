package com.expense.service.email;

import com.expense.dto.ExpenseReportInfo;
import com.expense.enm.ReportType;

public interface ExpensesReportEmailService {

    void sendReport(ExpenseReportInfo expenseReportInfo, ReportType reportType);
}
