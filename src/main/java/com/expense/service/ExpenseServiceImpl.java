package com.expense.service;

import com.expense.dto.ExpenseReportInfo;
import com.expense.dto.ExpenseView;
import com.expense.dto.NewExpense;
import com.expense.enm.ReportType;
import com.expense.mapper.ExpenseMapper;
import com.expense.model.Expense;
import com.expense.respository.ExpenseRepository;
import com.expense.service.email.ExpensesReportEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Slf4j
@Service
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    ExpenseRepository expenseRepository;

    @Autowired
    ExpenseMapper expenseMapper;

    @Autowired
    ExpensesReportEmailService expensesReportEmailService;

    @Override
    public void generateExpenseReport(LocalDate initialDate, LocalDate endDate) {
        Period period = initialDate.until(endDate);
        int monthsRange = period.getMonths();

        if (monthsRange > 5) {
            String message = "Report period exceeded the allowed range";
            log.error(message);
            throw new RuntimeException(message);
        }

        List<Expense> expenses = expenseRepository.findByTimestampBetween(initialDate, endDate);

        expensesReportEmailService.sendReport(ExpenseReportInfo.builder()
                .expenses(expenses)
                .endDate(endDate)
                .initialDate(initialDate)
                .build(), ReportType.EXCEL);
    }

    @Override
    public ExpenseView generateExpense(NewExpense newExpense) {
        log.info("Registering new expense {}", newExpense);
        Expense expense = expenseMapper.mapToExpense(newExpense);
        Expense registeredExpense = expenseRepository.save(expense);
        return expenseMapper.mapToExpenseView(registeredExpense);
    }
}
