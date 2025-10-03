package com.example.ecommerce.soap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import java.math.BigDecimal;

/**
 * Simple SOAP client for testing the Order SOAP service.
 * This demonstrates how to consume the SOAP service programmatically.
 */
@Component
@Slf4j
public class SoapTestClient {

    private final WebServiceTemplate webServiceTemplate;

    public SoapTestClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
        // Set the default URI for the SOAP service
        this.webServiceTemplate.setDefaultUri("http://localhost:8080/ws");
    }

    public GetOrderResponse getOrder(String orderId) {
        log.info("Calling SOAP service to get order: {}", orderId);
        
        GetOrderRequest request = new GetOrderRequest();
        request.setOrderId(orderId);
        
        GetOrderResponse response = (GetOrderResponse) webServiceTemplate.marshalSendAndReceive(request);
        
        log.info("Received SOAP response for order: {}", response.getOrder().getId());
        return response;
    }

    public CreateOrderResponse createOrder(String userId, BigDecimal totalAmount, String status) {
        log.info("Calling SOAP service to create order for user: {}", userId);
        
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(userId);
        request.setTotalAmount(totalAmount);
        request.setStatus(status);
        
        CreateOrderResponse response = (CreateOrderResponse) webServiceTemplate.marshalSendAndReceive(request);
        
        log.info("Created order via SOAP: {}", response.getOrder().getId());
        return response;
    }

    public UpdateOrderStatusResponse updateOrderStatus(String orderId, String status) {
        log.info("Calling SOAP service to update order status: {} -> {}", orderId, status);
        
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setOrderId(orderId);
        request.setStatus(status);
        
        UpdateOrderStatusResponse response = (UpdateOrderStatusResponse) webServiceTemplate.marshalSendAndReceive(request);
        
        log.info("Updated order status via SOAP: {}", response.getMessage());
        return response;
    }
}