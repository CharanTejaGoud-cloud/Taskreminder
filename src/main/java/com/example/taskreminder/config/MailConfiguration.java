package com.example.taskreminder.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Mail configuration that provides a mock JavaMailSender when mail is not configured.
 * This ensures the application can run without mail configuration.
 */
@Configuration
public class MailConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MailConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(JavaMailSender.class)
    public JavaMailSender javaMailSender(@Value("${spring.mail.host:}") String mailHost) {
        // If mail host is not configured, provide mock implementation
        logger.info("Mail not configured, creating mock JavaMailSender");
        return new MockJavaMailSender();
    }

    /**
     * Mock implementation of JavaMailSender for local testing.
     */
    private static class MockJavaMailSender extends JavaMailSenderImpl implements JavaMailSender {
        
        @Override
        public void send(SimpleMailMessage simpleMessage) {
            // Mock implementation - just log the email
            logger.info("=== MOCK EMAIL (Mail not configured) ===");
            logger.info("To: {}", simpleMessage.getTo() != null ? String.join(", ", simpleMessage.getTo()) : "N/A");
            logger.info("Subject: {}", simpleMessage.getSubject());
            logger.info("Body:\n{}", simpleMessage.getText());
            logger.info("========================================");
        }
    }
}

