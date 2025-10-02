package com.example.ecommerce.decorator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Push notification decorator.
 * Adds push notification functionality to the notification service.
 */
@Component
@Slf4j
public class PushNotificationDecorator extends NotificationDecorator {
    
    public PushNotificationDecorator(NotificationService notificationService) {
        super(notificationService);
    }
    
    @Override
    public void sendNotification(String recipient, String subject, String message) {
        super.sendNotification(recipient, subject, message);
        sendPushNotification(recipient, subject, message);
    }
    
    private void sendPushNotification(String recipient, String subject, String message) {
        // In a real implementation, you'd integrate with Firebase Cloud Messaging, Apple Push Notification service, etc.
        log.info("🔔 PUSH: Sending push notification to {}", recipient);
        log.info("🔔 PUSH: Title: {}", subject);
        log.info("🔔 PUSH: Body: {}", message);
        
        // Simulate push notification sending
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("🔔 PUSH: Successfully sent to {}", recipient);
    }
    
    @Override
    public String getNotificationDetails() {
        return super.getNotificationDetails() + " + Push";
    }
}