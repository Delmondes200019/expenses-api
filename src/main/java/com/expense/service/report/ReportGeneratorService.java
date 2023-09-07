package com.expense.service.report;

import com.expense.dto.ExpenseReportInfo;
import com.expense.enm.ReportType;
import org.springframework.core.io.FileSystemResource;

public interface ReportGeneratorService {

    FileSystemResource generate(ExpenseReportInfo expenseReportInfo);

    boolean supports(ReportType reportType);
}
