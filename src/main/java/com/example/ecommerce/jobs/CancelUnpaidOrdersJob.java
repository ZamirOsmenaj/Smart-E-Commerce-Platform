package com.example.ecommerce.jobs;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.domain.enums.OrderStatus;
import com.example.ecommerce.observer.OrderStatusPublisher;
import com.example.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * Quartz job that cancels orders which remain unpaid beyond a cutoff time.
 *
 * <p>
 * The job queries for orders with {@link OrderStatus#PENDING} created
 * before a configurable threshold (currently 1 hour) and:
 * <ul>
 *   <li>Sets their status to {@link OrderStatus#CANCELLED}</li>
 *   <li>Persists the update in the {@link OrderRepository}</li>
 *   <li>Releases reserved stock for each order item via {@link InventoryService}</li>
 * </ul>
 *
 * <p>
 * This helps keep the system clean of stale orders and ensures
 * inventory is not locked indefinitely by unpaid orders.
 */
@Component
@RequiredArgsConstructor
public class CancelUnpaidOrdersJob implements Job {

    private final OrderRepository orderRepository;
    private final OrderStatusPublisher orderStatusPublisher;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // Cutoff e.g., 1 hour
        Instant cutOff = Instant.now().minusSeconds(3600);
        List<Order> toCancel = orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.PENDING, cutOff);

        for (Order order : toCancel) {
            OrderStatus oldStatus = order.getStatus();
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);

            // Notify observers of the status change - they will handle inventory release and notifications
            orderStatusPublisher.notifyStatusChange(order, oldStatus, OrderStatus.CANCELLED);
        }
    }

}
