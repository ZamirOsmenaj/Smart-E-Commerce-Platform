package com.example.ecommerce.decorator;

import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Factory for creating decorated notification services.
 * Demonstrates the Decorator pattern by allowing dynamic composition of notification channels.
 */
@Component
public class NotificationServiceFactory {
    
    private final BasicNotificationService basicService;
    
    public NotificationServiceFactory(BasicNotificationService basicService) {
        this.basicService = basicService;
    }
    
    /**
     * Creates a notification service with specified channels.
     * 
     * @param channels List of notification channels to include
     * @return Decorated notification service
     */
    public NotificationService createNotificationService(List<NotificationChannel> channels) {
        NotificationService service = basicService;
        
        for (NotificationChannel channel : channels) {
            service = decorateWithChannel(service, channel);
        }
        
        return service;
    }
    
    /**
     * Creates a notification service for order-related notifications.
     * Email (base) + Push notifications.
     */
    public NotificationService createOrderNotificationService() {
        return createNotificationService(List.of(
            NotificationChannel.PUSH
        ));
    }
    
    /**
     * Creates a notification service for urgent notifications.
     * Email (base) + SMS + Push + Slack for maximum reach.
     */
    public NotificationService createUrgentNotificationService() {
        return createNotificationService(List.of(
            NotificationChannel.SMS,
            NotificationChannel.PUSH,
            NotificationChannel.SLACK
        ));
    }
    
    /**
     * Creates a notification service for admin notifications.
     * Email (base) + Slack for internal communications.
     */
    public NotificationService createAdminNotificationService() {
        return createNotificationService(List.of(
            NotificationChannel.SLACK
        ));
    }
    
    private NotificationService decorateWithChannel(NotificationService service, NotificationChannel channel) {
        return switch (channel) {
            case EMAIL -> service; // Email is already the base service
            case SMS -> new SmsNotificationDecorator(service);
            case PUSH -> new PushNotificationDecorator(service);
            case SLACK -> new SlackNotificationDecorator(service);
        };
    }
    
    public enum NotificationChannel {
        EMAIL, SMS, PUSH, SLACK
    }
}