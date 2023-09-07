package com.expense.service.email;

import com.expense.dto.ExpenseReportInfo;
import com.expense.enm.ReportType;
import com.expense.service.report.ReportGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ExpensesReportEmailServiceImpl implements ExpensesReportEmailService {

    @Autowired
    JavaMailSender emailSender;

    @Value(value = "${EMAIL_USERNAME}")
    String sender;

    @Value("${expenses-report.receiver-email}")
    String receiverEmail;

    @Autowired
    List<ReportGeneratorService> reportGeneratorServices;

    @Override
    public void sendReport(ExpenseReportInfo expenseReportInfo, ReportType reportType) {
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            String formattedInitialDate = dateFormatter.format(expenseReportInfo.getInitialDate());
            String formattedEndDate = dateFormatter.format(expenseReportInfo.getEndDate());

            String emailText = "Hi, this is your expenses report in the period ".concat(formattedInitialDate).concat(" ").concat(formattedEndDate);
            String emailSubject = "Expenses Report - ".concat(formattedInitialDate).concat(" to ").concat(formattedEndDate);

            ReportGeneratorService reportGeneratorService = reportGeneratorServices.stream()
                    .filter(service -> service.supports(reportType))
                    .findFirst().orElseThrow(() -> new RuntimeException("No generator for report type ".concat(reportType.name().concat(" found"))));

            FileSystemResource reportFile = reportGeneratorService.generate(expenseReportInfo);

            MimeMessage message = emailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sender);
            helper.setText(emailText);
            helper.setTo(receiverEmail);
            helper.setSubject(emailSubject);

            log.info(reportFile.getFilename());

            helper.addAttachment(Objects.requireNonNull(reportFile.getFilename()), reportFile);

            log.info("Sending expenses report email to {} from {}", receiverEmail, sender);

            emailSender.send(message);

            log.info("Email sent");

        } catch (Exception err) {
            err.printStackTrace();
            log.error(err.getMessage());
            throw new RuntimeException(err);
        }
    }

}
