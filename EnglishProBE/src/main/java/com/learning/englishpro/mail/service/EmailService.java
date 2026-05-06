package com.learning.englishpro.mail.service;

public interface EmailService {
    void sendOrderInvoiceEmail(Long orderId);
    void sendStudyReminderEmail(String toEmail, String studentName, String courseName);
}
