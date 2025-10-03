package com.example.ecommerce.controller;

import com.example.ecommerce.soap.CreateOrderResponse;
import com.example.ecommerce.soap.GetOrderResponse;
import com.example.ecommerce.soap.SoapTestClient;
import com.example.ecommerce.soap.UpdateOrderStatusResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * REST Controller that demonstrates integration with SOAP services.
 * This shows how REST and SOAP can work together in the same application.
 */
@RestController
@RequestMapping("/api/soap-integration")
@RequiredArgsConstructor
@Slf4j
public class SoapIntegrationController {

    private final SoapTestClient soapTestClient;

    /**
     * REST endpoint that calls the SOAP service to get an order.
     * This demonstrates REST-to-SOAP integration.
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<GetOrderResponse> getOrderViaSoap(@PathVariable String orderId) {
        log.info("REST request received to get order via SOAP: {}", orderId);
        
        try {
            GetOrderResponse response = soapTestClient.getOrder(orderId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error calling SOAP service: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * REST endpoint that calls the SOAP service to create an order.
     */
    @PostMapping("/orders")
    public ResponseEntity<CreateOrderResponse> createOrderViaSoap(@RequestBody Map<String, Object> orderData) {
        log.info("REST request received to create order via SOAP");
        
        try {
            String userId = (String) orderData.get("userId");
            BigDecimal totalAmount = new BigDecimal(orderData.get("totalAmount").toString());
            String status = (String) orderData.get("status");
            
            CreateOrderResponse response = soapTestClient.createOrder(userId, totalAmount, status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error calling SOAP service: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * REST endpoint that calls the SOAP service to update order status.
     */
    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<UpdateOrderStatusResponse> updateOrderStatusViaSoap(
            @PathVariable String orderId, 
            @RequestBody Map<String, String> statusData) {
        log.info("REST request received to update order status via SOAP: {}", orderId);
        
        try {
            String status = statusData.get("status");
            UpdateOrderStatusResponse response = soapTestClient.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error calling SOAP service: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Health check endpoint to verify SOAP service integration.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "message", "SOAP integration is working",
            "wsdl", "http://localhost:8080/ws/orders.wsdl"
        ));
    }
}