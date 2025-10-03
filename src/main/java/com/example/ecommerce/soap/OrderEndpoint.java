package com.example.ecommerce.soap;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.enums.OrderStatus;
import com.example.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.UUID;

@Endpoint
@RequiredArgsConstructor
@Slf4j
public class OrderEndpoint {

    private static final String NAMESPACE_URI = "http://example.com/ecommerce/orders";
    
    private final OrderService orderService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getOrderRequest")
    @ResponsePayload
    public GetOrderResponse getOrder(@RequestPayload GetOrderRequest request) {
        log.info("SOAP request received for order ID: {}", request.getOrderId());
        
        try {
            UUID orderId = UUID.fromString(request.getOrderId());
            Order order = orderService.findById(orderId);
            
            GetOrderResponse response = new GetOrderResponse();
            response.setOrder(convertToSoapOrder(order));
            
            log.info("SOAP response sent for order ID: {}", orderId);
            return response;
        } catch (Exception e) {
            log.error("Error processing SOAP getOrder request: {}", e.getMessage());
            throw new RuntimeException("Order not found or invalid ID format");
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createOrderRequest")
    @ResponsePayload
    public CreateOrderResponse createOrder(@RequestPayload CreateOrderRequest request) {
        log.info("SOAP request received to create order for user: {}", request.getUserId());
        
        try {
            UUID userId = UUID.fromString(request.getUserId());
            BigDecimal totalAmount = request.getTotalAmount();
            OrderStatus status = OrderStatus.valueOf(request.getStatus().toUpperCase());
            
            // Create order using existing service
            Order order = Order.builder()
                    .userId(userId)
                    .total(totalAmount)
                    .status(status)
                    .createdAt(Instant.now())
                    .build();
            
            Order savedOrder = orderService.save(order);
            
            CreateOrderResponse response = new CreateOrderResponse();
            response.setOrder(convertToSoapOrder(savedOrder));
            
            log.info("SOAP response sent for created order ID: {}", savedOrder.getId());
            return response;
        } catch (Exception e) {
            log.error("Error processing SOAP createOrder request: {}", e.getMessage());
            throw new RuntimeException("Failed to create order: " + e.getMessage());
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateOrderStatusRequest")
    @ResponsePayload
    public UpdateOrderStatusResponse updateOrderStatus(@RequestPayload UpdateOrderStatusRequest request) {
        log.info("SOAP request received to update order status: {} -> {}", 
                request.getOrderId(), request.getStatus());
        
        UpdateOrderStatusResponse response = new UpdateOrderStatusResponse();
        
        try {
            UUID orderId = UUID.fromString(request.getOrderId());
            OrderStatus newStatus = OrderStatus.valueOf(request.getStatus().toUpperCase());
            
            Order order = orderService.findById(orderId);
            order.setStatus(newStatus);
            orderService.save(order);
            
            response.setSuccess(true);
            response.setMessage("Order status updated successfully");
            
            log.info("SOAP response sent for order status update: {}", orderId);
        } catch (Exception e) {
            log.error("Error processing SOAP updateOrderStatus request: {}", e.getMessage());
            response.setSuccess(false);
            response.setMessage("Failed to update order status: " + e.getMessage());
        }
        
        return response;
    }

    private com.example.ecommerce.soap.Order convertToSoapOrder(Order domainOrder) {
        com.example.ecommerce.soap.Order soapOrder = new com.example.ecommerce.soap.Order();
        soapOrder.setId(domainOrder.getId().toString());
        soapOrder.setUserId(domainOrder.getUserId().toString());
        soapOrder.setTotalAmount(domainOrder.getTotal());
        soapOrder.setStatus(domainOrder.getStatus().name());
        soapOrder.setCreatedAt(convertToXMLGregorianCalendar(domainOrder.getCreatedAt()));
        
        return soapOrder;
    }

    private XMLGregorianCalendar convertToXMLGregorianCalendar(Instant instant) {
        try {
            ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
            GregorianCalendar gregorianCalendar = GregorianCalendar.from(zonedDateTime);
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Error converting date", e);
        }
    }
}