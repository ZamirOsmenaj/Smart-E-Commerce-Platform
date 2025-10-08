package com.example.ecommerce.soap;

import com.example.ecommerce.command.CommandResult;
import com.example.ecommerce.constants.MessageConstants;
import com.example.ecommerce.domain.Order;
import com.example.ecommerce.enums.OrderStatus;
import com.example.ecommerce.dto.request.CreateOrderRequestDTO;
import com.example.ecommerce.dto.response.OrderResponseDTO;
import com.example.ecommerce.service.JwtService;
import com.example.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.server.endpoint.annotation.SoapHeader;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Endpoint
@RequiredArgsConstructor
@Slf4j
public class OrderEndpoint {

    private static final String NAMESPACE_URI = "http://example.com/ecommerce/orders";
    
    private final OrderService orderService;
    private final JwtService jwtService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getOrderRequest")
    @ResponsePayload
    public GetOrderResponse getOrder(
            @RequestPayload GetOrderRequest request,
            @SoapHeader("{http://example.com/ecommerce/orders}authToken") SoapHeaderElement authHeader) {
        
        log.info("SOAP request received for order ID: {}", request.getOrderId());
        
        try {
            // Extract and validate JWT token
            UUID userId = extractAndValidateUser(authHeader);
            
            UUID orderId = UUID.fromString(request.getOrderId());
            OrderResponseDTO orderDto = orderService.getById(orderId);
            
            // Verify user owns the order
            if (!orderDto.getUserId().equals(userId)) {
                log.warn("SOAP: User {} attempted to access order {} owned by {}", 
                        userId, orderId, orderDto.getUserId());
                throw new RuntimeException(MessageConstants.ORDER_ACCESS_DENIED_DETAILED);
            }
            
            GetOrderResponse response = new GetOrderResponse();
            response.setOrder(convertToSoapOrder(orderDto));
            
            log.info("SOAP response sent for order ID: {}", orderId);
            return response;
        } catch (Exception e) {
            log.error("Error processing SOAP getOrder request: {}", e.getMessage());
            throw new RuntimeException(MessageConstants.ORDER_ACCESS_SOAP_FAILED + ": " + e.getMessage());
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createOrderRequest")
    @ResponsePayload
    public CreateOrderResponse createOrder(
            @RequestPayload CreateOrderRequest request,
            @SoapHeader("{http://example.com/ecommerce/orders}authToken") SoapHeaderElement authHeader) {
        
        log.info("SOAP request received to create order with {} items", 
                request.getItems().getItem().size());
        
        try {
            // Extract and validate JWT token
            UUID userId = extractAndValidateUser(authHeader);
            
            // Convert SOAP request to DTO (same as REST API)
            CreateOrderRequestDTO orderRequestDto = new CreateOrderRequestDTO();
            List<CreateOrderRequestDTO.Item> items = request.getItems().getItem().stream()
                    .map(soapItem -> {
                        CreateOrderRequestDTO.Item item = new CreateOrderRequestDTO.Item();
                        item.setProductId(UUID.fromString(soapItem.getProductId()));
                        item.setQuantity(soapItem.getQuantity());
                        return item;
                    })
                    .collect(Collectors.toList());
            orderRequestDto.setItems(items);
            
            // Use the same business logic as REST API
            CommandResult result = orderService.createOrderWithCommand(userId, orderRequestDto);
            
            if (!result.isSuccess()) {
                log.error("SOAP: Order creation failed: {}", result.getMessage());
                throw new RuntimeException(MessageConstants.ORDER_CREATION_FAILED + ": " + result.getMessage());
            }

            OrderResponseDTO orderDto = (OrderResponseDTO) result.getData();

            CreateOrderResponse response = new CreateOrderResponse();
            response.setOrder(convertToSoapOrder(orderDto));
            
            log.info("SOAP response sent for created order ID: {}", orderDto.getId());
            return response;
        } catch (Exception e) {
            log.error("Error processing SOAP createOrder request: {}", e.getMessage());
            throw new RuntimeException(MessageConstants.ORDER_CREATION_SOAP_FAILED + ": " + e.getMessage());
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateOrderStatusRequest")
    @ResponsePayload
    public UpdateOrderStatusResponse updateOrderStatus(
            @RequestPayload UpdateOrderStatusRequest request,
            @SoapHeader("{http://example.com/ecommerce/orders}authToken") SoapHeaderElement authHeader) {
        
        log.info("SOAP request received to update order status: {} -> {}", 
                request.getOrderId(), request.getStatus());
        
        UpdateOrderStatusResponse response = new UpdateOrderStatusResponse();
        
        try {
            // Extract and validate JWT token
            UUID userId = extractAndValidateUser(authHeader);
            
            UUID orderId = UUID.fromString(request.getOrderId());
            OrderResponseDTO orderDto = orderService.getById(orderId);
            
            // Verify user owns the order
            if (!orderDto.getUserId().equals(userId)) {
                log.warn("SOAP: User {} attempted to update order {} owned by {}", 
                        userId, orderId, orderDto.getUserId());
                throw new RuntimeException(MessageConstants.ORDER_ACCESS_DENIED_DETAILED);
            }
            
            OrderStatus newStatus = OrderStatus.valueOf(request.getStatus().toUpperCase());
            
            // Use proper business logic (could extend to use command pattern)
            Order order = orderService.findById(orderId);
            order.setStatus(newStatus);
            orderService.save(order);
            
            response.setSuccess(true);
            response.setMessage(MessageConstants.ORDER_STATUS_UPDATED_SUCCESS);
            
            log.info("SOAP response sent for order status update: {}", orderId);
        } catch (Exception e) {
            log.error("Error processing SOAP updateOrderStatus request: {}", e.getMessage());
            response.setSuccess(false);
            response.setMessage(MessageConstants.ORDER_STATUS_UPDATE_FAILED + ": " + e.getMessage());
        }
        
        return response;
    }

    /**
     * Extracts and validates JWT token from SOAP header.
     * Follows the same authentication pattern as REST API.
     */
    private UUID extractAndValidateUser(SoapHeaderElement authHeader) {
        if (authHeader == null) {
            throw new RuntimeException(MessageConstants.AUTHENTICATION_REQUIRED + ": " + MessageConstants.MISSING_AUTH_TOKEN);
        }
        
        String token = authHeader.getText();
        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException(MessageConstants.AUTHENTICATION_REQUIRED + ": " + MessageConstants.EMPTY_AUTH_TOKEN);
        }
        
        // Remove Bearer prefix if present (same as REST API)
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        try {
            return jwtService.extractUserId(token);
        } catch (Exception e) {
            log.error("SOAP: JWT validation failed: {}", e.getMessage());
            throw new RuntimeException(MessageConstants.AUTHENTICATION_FAILED + ": " + MessageConstants.TOKEN_INVALID);
        }
    }

    private com.example.ecommerce.soap.Order convertToSoapOrder(OrderResponseDTO orderDto) {
        com.example.ecommerce.soap.Order soapOrder = new com.example.ecommerce.soap.Order();
        soapOrder.setId(orderDto.getId().toString());
        soapOrder.setUserId(orderDto.getUserId().toString());
        soapOrder.setTotalAmount(orderDto.getTotal());
        soapOrder.setStatus(orderDto.getStatus().name());
        soapOrder.setCreatedAt(convertToXMLGregorianCalendar(orderDto.getCreatedAt()));
        
        // Convert order items if present
        if (orderDto.getItems() != null && !orderDto.getItems().isEmpty()) {
            OrderItems soapItems = new OrderItems();
            List<OrderItem> soapItemList = orderDto.getItems().stream()
                    .map(item -> {
                        OrderItem soapItem = new OrderItem();
                        soapItem.setProductId(item.getProductId().toString());
                        soapItem.setQuantity(item.getQuantity());
                        return soapItem;
                    })
                    .toList();
            soapItems.getItem().addAll(soapItemList);
            soapOrder.setItems(soapItems);
        }
        
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