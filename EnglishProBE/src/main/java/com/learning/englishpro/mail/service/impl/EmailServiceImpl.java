package com.learning.englishpro.mail.service.impl;

import com.learning.englishpro.mail.service.EmailService;
import com.learning.englishpro.payment.entity.Order;
import com.learning.englishpro.payment.repository.OrderRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;
    private final OrderRepository orderRepository;
    private final com.learning.englishpro.setting.service.AppSettingService appSettingService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Runs asynchronously. Accepts only the orderId to avoid passing detached
     * Hibernate entities across thread boundaries (LazyInitializationException).
     * Fetches the Order fresh in its own @Transactional context with all
     * associations eagerly loaded via a JOIN FETCH query.
     */
    @Async
    @Transactional(readOnly = true)
    @Override
    public void sendOrderInvoiceEmail(Long orderId) {
        log.info("Starting to send invoice email for orderId={}", orderId);

        String isEnabled = appSettingService.getSetting("notifyNewOrder", "true");
        if (!"true".equalsIgnoreCase(isEnabled)) {
            log.info("Invoice email is disabled in settings. Skipping for orderId={}", orderId);
            return;
        }

        // Re-fetch the order in this new transaction with all needed associations
        Order order = orderRepository.findByIdWithDetails(orderId).orElse(null);
        if (order == null) {
            log.error("Cannot send invoice email: order {} not found", orderId);
            return;
        }

        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(order.getUser().getEmail());
            helper.setSubject("EnglishPro - Xác nhận mua khóa học thành công [" + order.getOrderCode() + "]");

            // Prepare Thymeleaf context
            Context context = new Context();
            Map<String, Object> templateModel = new HashMap<>();

            DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern("dd/MM/yyyy HH:mm")
                    .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));
            String formattedDate = formatter.format(order.getCreatedAt());

            String customerName = (order.getUser().getProfile() != null
                    && order.getUser().getProfile().getFullName() != null)
                    ? order.getUser().getProfile().getFullName()
                    : order.getUser().getUsername();

            templateModel.put("customerName", customerName);
            templateModel.put("orderCode", order.getOrderCode());
            templateModel.put("orderDate", formattedDate);
            templateModel.put("courseName", order.getCourse().getTitle());
            templateModel.put("originalPrice", String.format("%,.0f ₫", order.getAmount()));

            if (order.getDiscountAmount() != null && order.getDiscountAmount().signum() > 0) {
                templateModel.put("discountAmount", String.format("- %,.0f ₫", order.getDiscountAmount()));
                templateModel.put("hasDiscount", true);
            } else {
                templateModel.put("hasDiscount", false);
            }

            templateModel.put("finalAmount", String.format("%,.0f ₫", order.getFinalAmount()));

            context.setVariables(templateModel);

            String htmlContent = templateEngine.process("invoice-email", context);
            helper.setText(htmlContent, true);

            emailSender.send(message);
            log.info("Invoice email sent successfully to {}", order.getUser().getEmail());

        } catch (MessagingException e) {
            log.error("Failed to send invoice email for order {}", orderId, e);
        } catch (Exception e) {
            log.error("Unexpected error while sending invoice email for order {}", orderId, e);
        }
    }

    @Async
    @Override
    public void sendStudyReminderEmail(String toEmail, String studentName, String courseName) {
        log.info("Starting to send study reminder email to {}", toEmail);
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("EnglishPro - Nhắc nhở học tập: " + courseName);

            Context context = new Context();
            Map<String, Object> templateModel = new HashMap<>();
            templateModel.put("studentName", studentName);
            templateModel.put("courseName", courseName);
            context.setVariables(templateModel);

            String htmlContent = templateEngine.process("study-reminder-email", context);
            helper.setText(htmlContent, true);

            emailSender.send(message);
            log.info("Study reminder email sent successfully to {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send study reminder email to {}", toEmail, e);
        } catch (Exception e) {
            log.error("Unexpected error while sending study reminder email to {}", toEmail, e);
        }
    }
}
