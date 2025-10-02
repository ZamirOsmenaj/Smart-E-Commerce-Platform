package com.example.ecommerce.decorator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * SMS notification decorator.
 * Adds SMS functionality to the notification service.
 */
@Component
@Slf4j
public class SmsNotificationDecorator extends NotificationDecorator {
    
    public SmsNotificationDecorator(NotificationService notificationService) {
        super(notificationService);
    }
    
    @Override
    public void sendNotification(String recipient, String subject, String message) {
        super.sendNotification(recipient, subject, message);
        sendSms(recipient, message);
    }
    
    private void sendSms(String recipient, String message) {
        // In a real implementation, you'd integrate with Twilio, AWS SNS, etc.
        log.info("📱 SMS: Sending SMS to {}", recipient);
        
        // SMS messages are typically shorter
        String smsMessage = message.length() > 160 ? 
            message.substring(0, 157) + "..." : message;
        
        log.info("📱 SMS: Message: {}", smsMessage);
        
        // Simulate SMS sending delay
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("📱 SMS: Successfully sent to {}", recipient);
    }
    
    @Override
    public String getNotificationDetails() {
        return super.getNotificationDetails() + " + SMS";
    }
}