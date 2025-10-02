package com.example.ecommerce.decorator;

/**
 * Abstract base decorator for notification services.
 * This is the base decorator in the Decorator pattern.
 */
public abstract class NotificationDecorator implements NotificationService {
    
    protected NotificationService wrappedService;
    
    public NotificationDecorator(NotificationService notificationService) {
        this.wrappedService = notificationService;
    }
    
    @Override
    public void sendNotification(String recipient, String subject, String message) {
        wrappedService.sendNotification(recipient, subject, message);
    }
    
    @Override
    public String getNotificationDetails() {
        return wrappedService.getNotificationDetails();
    }
}