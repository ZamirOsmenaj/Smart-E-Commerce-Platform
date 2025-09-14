package com.example.ecommerce.service;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.OrderItem;
import com.example.ecommerce.domain.enums.OrderStatus;
import com.example.ecommerce.payment.MockPaymentController;
import com.example.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RestTemplate restTemplate;
    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;

    /**
     * Attempts to process payment for the given orderId.
     * If approved -> sets order status to PAID.
     * If declined -> sets order status to CANCELLED and releases reserved stock.
     *
     * @param orderId
     *
     * @return true if payment approved.
     */
    @Transactional
    public void processPayment(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found!"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order is not in PENDING state!");
        }

        MockPaymentController.ProcessPaymentRequest req = new MockPaymentController.ProcessPaymentRequest();
        req.setOrderId(orderId.toString());
        req.setAmount(order.getTotal());

        String PAYMENT_PROVIDER_URL = "http://localhost:8080/api/mock-payments/process";
        ResponseEntity<MockPaymentController.ProcessPaymentResponse> resp =
                restTemplate.postForEntity(PAYMENT_PROVIDER_URL, req, MockPaymentController.ProcessPaymentResponse.class);

        String status = resp.getBody() != null ? resp.getBody().getStatus() : "DECLINED";

        if ("APPROVED".equalsIgnoreCase(status)) {
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);
        } else {
            // payment declined => cancel order and release reserved stock
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);

            for (OrderItem item : order.getItems()) {
                inventoryService.releaseStock(item.getProductId(), item.getQuantity());
            }
        }
    }

}
