package com.expense.mapper;

import com.expense.dto.ExpenseView;
import com.expense.dto.NewExpense;
import com.expense.model.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    @Mapping(target = "timestamp", source = "date")
    Expense mapToExpense(NewExpense newExpense);

    @Mapping(target = "date", source = "timestamp")
    ExpenseView mapToExpenseView(Expense expense);
}
