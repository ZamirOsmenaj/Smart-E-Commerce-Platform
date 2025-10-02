package com.example.ecommerce.decorator;

/**
 * Base interface for notification services.
 * This is the component interface in the Decorator pattern.
 */
public interface NotificationService {
    void sendNotification(String recipient, String subject, String message);
    String getNotificationDetails();
}