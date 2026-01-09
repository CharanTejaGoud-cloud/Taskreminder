package com.example.taskreminder.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending email notifications.
 * Uses JavaMailSender when configured, otherwise provides mock implementation.
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final boolean mailConfigured;

    @Autowired
    public EmailService(JavaMailSender mailSender, 
                       @Value("${spring.mail.host:}") String mailHost) {
        this.mailSender = mailSender;
        // Check if mail is configured by checking if host is set
        this.mailConfigured = mailHost != null && !mailHost.isEmpty();
        if (!mailConfigured) {
            logger.info("Mail not configured, using mock email sender");
        }
    }

    /**
     * Send reminder email for a task.
     */
    public void sendReminderEmail(String to, String taskTitle, String taskDescription, Long dueTimestamp) {
        String subject = "Task Reminder: " + taskTitle;
        String body = String.format(
            "This is a reminder for your task:\n\n" +
            "Title: %s\n" +
            "Description: %s\n" +
            "Due Date: %s\n\n" +
            "Please complete this task before the due date.",
            taskTitle,
            taskDescription != null ? taskDescription : "No description",
            formatTimestamp(dueTimestamp)
        );

        sendEmail(to, subject, body);
    }

    /**
     * Send completion notification email.
     */
    public void sendCompletionEmail(String to, String taskTitle) {
        String subject = "Task Completed: " + taskTitle;
        String body = String.format(
            "Congratulations! You have completed the following task:\n\n" +
            "Title: %s\n" +
            "Completed at: %s",
            taskTitle,
            formatTimestamp(System.currentTimeMillis())
        );

        sendEmail(to, subject, body);
    }

    /**
     * Send email using JavaMailSender or mock.
     */
    private void sendEmail(String to, String subject, String body) {
        if (to == null || to.isEmpty()) {
            logger.warn("No email address provided, skipping email send");
            return;
        }

        try {
            if (mailConfigured) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
                logger.info("Email sent successfully to: {}", to);
            } else {
                // Mock email sender for local testing
                logger.info("=== MOCK EMAIL (Mail not configured) ===");
                logger.info("To: {}", to);
                logger.info("Subject: {}", subject);
                logger.info("Body:\n{}", body);
                logger.info("========================================");
            }
        } catch (MailException e) {
            logger.error("Failed to send email to: {}", to, e);
            // Fallback to mock if real mail fails
            logger.info("=== MOCK EMAIL (Fallback) ===");
            logger.info("To: {}", to);
            logger.info("Subject: {}", subject);
            logger.info("Body:\n{}", body);
            logger.info("========================================");
        }
    }

    /**
     * Format timestamp to readable string.
     */
    private String formatTimestamp(Long timestamp) {
        if (timestamp == null) {
            return "Not set";
        }
        return new java.util.Date(timestamp).toString();
    }
}

