package com.expense.service.report;

import com.expense.dto.ExpenseReportInfo;
import com.expense.enm.ReportType;
import com.expense.model.Expense;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ExcelReportGeneratorImpl implements ReportGeneratorService {

    @Override
    public FileSystemResource generate(ExpenseReportInfo expenseReportInfo) {
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            String formattedInitialDate = dateFormatter.format(expenseReportInfo.getInitialDate());
            String formattedEndDate = dateFormatter.format(expenseReportInfo.getEndDate());

            List<Expense> expenses = expenseReportInfo.getExpenses();

            String fileId = UUID.randomUUID().toString();
            String fileName = fileId.concat("-").concat(formattedInitialDate).concat("-").concat(formattedEndDate);
            String tempFilePath = Files.createTempFile(fileName, ".xlsx").toAbsolutePath().toString();
            String sheetName = "expenses-report-".concat(formattedInitialDate).concat(" ").concat(formattedEndDate);

            FileOutputStream fileOutputStream = new FileOutputStream(new File(tempFilePath));

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet(sheetName);

            for (int rowNumber = 0; rowNumber < expenses.size(); rowNumber++){
                Expense expense = expenses.get(rowNumber);
                createExpenseRow(sheet, rowNumber, expense);
            }

            workbook.write(fileOutputStream);
            workbook.close();

            return new FileSystemResource(tempFilePath);

        } catch (Exception e){
            e.printStackTrace();
            log.error("Error generating excel report ".concat(e.getMessage()));
            throw new RuntimeException("Error generating excel report ");
        }
    }

    private static void createExpenseRow(Sheet sheet, int rowNumber, Expense expense) {
        Row row = sheet.createRow(rowNumber);
        Cell descriptionCell = row.createCell(0);
        descriptionCell.setCellValue(expense.getDescription());

        Cell totalCell = row.createCell(1);
        totalCell.setCellValue(expense.getTotal().toString());

        Cell dateCell = row.createCell(2);
        dateCell.setCellValue(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(expense.getTimestamp()));
    }

    @Override
    public boolean supports(ReportType reportType) {
        return ReportType.EXCEL.equals(reportType);
    }
}
