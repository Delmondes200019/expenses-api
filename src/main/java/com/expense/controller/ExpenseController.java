package com.expense.controller;

import com.expense.dto.ExpenseView;
import com.expense.dto.NewExpense;
import com.expense.service.ExpenseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/expense")
public class ExpenseController {

    @Autowired
    ExpenseService expenseService;

    @GetMapping("/report/period/email")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Map<String, Object>> generateExpensesReportEmail(
            @RequestParam("initial-date") @DateTimeFormat(pattern = "dd-MM-yyyy") @NotNull LocalDate initialDate,
            @RequestParam("end-date") @DateTimeFormat(pattern = "dd-MM-yyyy") @NotNull LocalDate endDate){

        log.info("Request to generate expense reports between {} and {} dates", initialDate, endDate);

        expenseService.generateExpenseReport(initialDate, endDate);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ExpenseView> createNewExpense(@RequestBody @Valid NewExpense newExpense){
        ExpenseView expenseView = expenseService.generateExpense(newExpense);
        return ResponseEntity.ok(expenseView);
    }
}
