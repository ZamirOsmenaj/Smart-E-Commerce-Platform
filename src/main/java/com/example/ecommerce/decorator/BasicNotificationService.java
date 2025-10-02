package com.example.ecommerce.decorator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Email notification service - the concrete component in the Decorator pattern.
 * This is the base notification service that actually sends emails.
 * In a real implementation, this would integrate with SendGrid, AWS SES, etc.
 */
@Component
@Primary
@Slf4j
public class BasicNotificationService implements NotificationService {
    
    @Override
    public void sendNotification(String recipient, String subject, String message) {
        sendEmail(recipient, subject, message);
    }
    
    private void sendEmail(String recipient, String subject, String message) {
        // In a real implementation, you'd integrate with an email service
        log.info("📧 EMAIL: Sending email to {}", recipient);
        log.info("📧 EMAIL: Subject: {}", subject);
        log.info("📧 EMAIL: Body: {}", message);
        
        // Simulate email sending delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("📧 EMAIL: Successfully sent to {}", recipient);
    }
    
    @Override
    public String getNotificationDetails() {
        return "Email";
    }
}