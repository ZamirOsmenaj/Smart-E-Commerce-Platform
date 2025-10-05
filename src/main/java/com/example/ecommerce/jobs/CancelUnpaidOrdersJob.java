package com.example.ecommerce.jobs;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * Quartz job that cancels orders which remain unpaid beyond a cutoff time.
 * 
 * IMPROVED ARCHITECTURE:
 * - Uses OrderService instead of direct repository access
 * - Follows proper layered architecture principles
 * - Configurable timeout via application properties
 * - Better error handling and logging
 *
 * The job uses {@link OrderService} to:
 * <ul>
 *   <li>Find unpaid orders older than the configured threshold</li>
 *   <li>Cancel orders using proper business logic</li>
 *   <li>Trigger observer notifications for inventory release</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CancelUnpaidOrdersJob implements Job {

    private final OrderService orderService;
    
    @Value("${app.orders.unpaid-timeout-minutes:60}")
    private int unpaidTimeoutMinutes;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            log.info("Starting unpaid orders cancellation job - timeout: {} minutes", unpaidTimeoutMinutes);
            
            // Calculate cutoff time based on configuration
            Instant cutOff = Instant.now().minusSeconds(unpaidTimeoutMinutes * 60L);
            
            // Use service layer instead of direct repository access
            List<Order> unpaidOrders = orderService.findUnpaidOrdersOlderThan(cutOff);
            
            if (unpaidOrders.isEmpty()) {
                log.info("No unpaid orders found older than {}", cutOff);
                return;
            }
            
            log.info("Found {} unpaid orders to cancel", unpaidOrders.size());
            
            // Use service method for bulk cancellation with proper business logic
            orderService.cancelOrders(unpaidOrders, "Automatic cancellation - payment timeout");
            
            log.info("Successfully cancelled {} unpaid orders", unpaidOrders.size());
            
        } catch (Exception e) {
            log.error("Error executing unpaid orders cancellation job", e);
            throw new JobExecutionException("Failed to cancel unpaid orders", e);
        }
    }
}
