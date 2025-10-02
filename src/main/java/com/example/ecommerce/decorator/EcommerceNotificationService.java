package com.example.ecommerce.decorator;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * E-commerce specific notification service that integrates with business operations.
 * Uses the Decorator pattern to send notifications through multiple channels.
 */
@Service
public class EcommerceNotificationService {
    
    private final NotificationServiceFactory notificationFactory;
    
    public EcommerceNotificationService(NotificationServiceFactory notificationFactory) {
        this.notificationFactory = notificationFactory;
    }
    
    /**
     * Sends order confirmation notification to customer.
     */
    public void sendOrderConfirmation(Order order, User customer) {
        NotificationService service = notificationFactory.createOrderNotificationService();
        
        String subject = "Order Confirmation - Order #" + order.getId();
        String message = String.format(
            "Thank you for your order!\n\n" +
            "Order ID: %s\n" +
            "Total Amount: $%.2f\n" +
            "Status: %s\n\n" +
            "We'll send you updates as your order progresses.",
            order.getId(),
            order.getTotal(),
            order.getStatus()
        );
        
        service.sendNotification(customer.getEmail(), subject, message);
    }
    
    /**
     * Sends order status update notification.
     */
    public void sendOrderStatusUpdate(Order order, User customer, String newStatus) {
        NotificationService service = notificationFactory.createOrderNotificationService();
        
        String subject = "Order Update - Order #" + order.getId();
        String message = String.format(
            "Hello,\n\n" +
            "Your order #%s has been updated.\n" +
            "New Status: %s\n\n" +
            "Thank you for shopping with us!",
            order.getId(),
            newStatus
        );
        
        service.sendNotification(customer.getEmail(), subject, message);
    }
    
    /**
     * Sends low inventory alert to administrators.
     */
    public void sendLowInventoryAlert(Product product, int currentStock, int threshold) {
        NotificationService service = notificationFactory.createAdminNotificationService();
        
        String subject = "Low Inventory Alert - " + product.getName();
        String message = String.format(
            "INVENTORY ALERT\n\n" +
            "Product: %s (ID: %s)\n" +
            "Current Stock: %d\n" +
            "Threshold: %d\n\n" +
            "Please restock this item soon.",
            product.getName(),
            product.getId(),
            currentStock,
            threshold
        );
        
        service.sendNotification("admin@ecommerce.com", subject, message);
    }
    
    /**
     * Sends payment failure notification.
     */
    public void sendPaymentFailureNotification(Order order, User customer, String reason) {
        NotificationService service = notificationFactory.createUrgentNotificationService();
        
        String subject = "Payment Issue - Order #" + order.getId();
        String message = String.format(
            "Hello,\n\n" +
            "We encountered an issue processing payment for your order #%s.\n" +
            "Reason: %s\n\n" +
            "Please update your payment method to complete your order.\n" +
            "Your items are reserved for 24 hours.",
            order.getId(),
            reason
        );

        service.sendNotification(customer.getEmail(), subject, message);
    }
    
    /**
     * Sends welcome notification to new users.
     */
    public void sendWelcomeNotification(User user) {
        NotificationService service = notificationFactory.createNotificationService(
            List.of(
                NotificationServiceFactory.NotificationChannel.PUSH
            )
        );
        
        String subject = "Welcome to Our E-commerce Store!";
        String message = """
                Welcome!
                
                Thank you for joining our e-commerce platform.
                You can now browse our products, add items to your cart, and place orders.
                
                Happy shopping!""";
        
        service.sendNotification(user.getEmail(), subject, message);
    }
}