package com.expense.config.expenses;

import com.expense.model.Expense;
import com.expense.respository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
@Transactional
public class ExpensesPersistence {

    @Autowired
    ExpenseRepository expenseRepository;

    @PostConstruct
    public void persistExpenses(){
        Expense monitor = Expense.builder()
                .description("Monitor DELL 16.9")
                .timestamp(LocalDate.of(2022, 3,12))
                .total(BigDecimal.valueOf(2100.00))
                .build();

        Expense notebook = Expense.builder()
                .description("Notebook DELL 14.5 6G RAM 124GB SSD 50GB HD")
                .timestamp(LocalDate.of(2022, 5,14))
                .total(BigDecimal.valueOf(2599.90))
                .build();

        Expense gamerChair = Expense.builder()
                .description("Cadeira Gamer Confort Line")
                .timestamp(LocalDate.of(2022, 8,27))
                .total(BigDecimal.valueOf(1699.99))
                .build();

        Expense officeTable = Expense.builder()
                .description("Mesa de escritório preta")
                .timestamp(LocalDate.of(2022, 3,22))
                .total(BigDecimal.valueOf(474.99))
                .build();

        expenseRepository.saveAll(List.of(monitor, gamerChair, officeTable, notebook));
    }
}
