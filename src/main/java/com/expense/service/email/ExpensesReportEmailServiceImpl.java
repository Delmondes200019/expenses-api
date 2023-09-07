package com.expense.service.email;

import com.expense.dto.ExpenseReportInfo;
import com.expense.model.Expense;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ExpensesReportEmailServiceImpl implements ExpensesReportEmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Value(value = "${EMAIL_USERNAME}")
    String sender;

    @Value("${expenses-report.receiver-email}")
    String receiverEmail;

    @Override
    public void sendReport(ExpenseReportInfo expenseReportInfo) {
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            String formattedInitialDate = dateFormatter.format(expenseReportInfo.getInitialDate());
            String formattedEndDate = dateFormatter.format(expenseReportInfo.getEndDate());

            String fileId = UUID.randomUUID().toString();
            String fileName = fileId.concat("-").concat(formattedInitialDate).concat("-").concat(formattedEndDate);

            String tempFilePath = createExcelReportFile(formattedInitialDate, formattedEndDate, fileName, expenseReportInfo);

            MimeMessage message = emailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sender);
            helper.setText("Hi, this is your expenses report in the period ".concat(formattedInitialDate).concat(" ").concat(formattedEndDate));
            helper.setTo(receiverEmail);
            helper.setSubject("Expenses Report - ".concat(formattedInitialDate).concat(" to ").concat(formattedEndDate));

            log.info(tempFilePath.toString());

            FileSystemResource file = new FileSystemResource(tempFilePath);

            helper.addAttachment(file.getFilename(), file);

            log.info("Sending expenses report email to {} from {}", receiverEmail, sender);

            emailSender.send(message);

            log.info("Email sent");

        } catch (Exception err) {
            err.printStackTrace();
            log.error(err.getMessage());
            throw new RuntimeException(err);
        }
    }

    private String createExcelReportFile(String formattedInitialDate, String formattedEndDate, String fileName, ExpenseReportInfo expenseReportInfo) throws IOException {
        List<Expense> expenses = expenseReportInfo.getExpenses();

        String tempFilePath = Files.createTempFile(fileName, ".xlsx").toAbsolutePath().toString();

        FileOutputStream fileOutputStream = new FileOutputStream(new File(tempFilePath));

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("expenses-report-".concat(formattedInitialDate).concat("-").concat(formattedEndDate));

        for (int rowNumber = 0; rowNumber < expenses.size(); rowNumber++){
            Expense expense = expenses.get(rowNumber);

            Row row = sheet.createRow(rowNumber);
            Cell descriptionCell = row.createCell(0);
            descriptionCell.setCellValue(expense.getDescription());

            Cell totalCell = row.createCell(1);
            totalCell.setCellValue(expense.getTotal().toString());

            Cell dateCell = row.createCell(2);
            dateCell.setCellValue(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(expense.getTimestamp()));
        }

        workbook.write(fileOutputStream);
        workbook.close();

        return tempFilePath;
    }
}
