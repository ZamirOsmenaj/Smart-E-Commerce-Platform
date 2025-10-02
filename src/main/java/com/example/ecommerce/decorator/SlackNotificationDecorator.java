package com.example.ecommerce.decorator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Slack notification decorator.
 * Adds Slack messaging functionality to the notification service.
 */
@Component
@Slf4j
public class SlackNotificationDecorator extends NotificationDecorator {
    
    public SlackNotificationDecorator(NotificationService notificationService) {
        super(notificationService);
    }
    
    @Override
    public void sendNotification(String recipient, String subject, String message) {
        super.sendNotification(recipient, subject, message);
        sendSlackMessage(recipient, subject, message);
    }
    
    private void sendSlackMessage(String recipient, String subject, String message) {
        // In a real implementation, you'd integrate with Slack Web API
        log.info("💬 SLACK: Sending Slack message to {}", recipient);
        
        String slackMessage = String.format("*%s*\n%s", subject, message);
        log.info("💬 SLACK: Message: {}", slackMessage);
        
        // Simulate Slack API call
        try {
            Thread.sleep(80);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("💬 SLACK: Successfully sent to {}", recipient);
    }
    
    @Override
    public String getNotificationDetails() {
        return super.getNotificationDetails() + " + Slack";
    }
}