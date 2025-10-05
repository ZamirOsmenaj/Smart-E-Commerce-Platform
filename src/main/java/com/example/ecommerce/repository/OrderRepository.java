package com.example.ecommerce.repository;

import com.example.ecommerce.domain.Order;
import com.example.ecommerce.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for mapping {@link Order} entities.
 *
 *  Extends {@link JpaRepository} to obtain standard CRUD operations.
 */
public interface OrderRepository extends JpaRepository<Order, UUID> {

    /**
     * Retrieves all orders associated with a specific user.
     *
     * @param userId the unique identifier of the user
     * @return a list of {@link Order} instances belonging to the user;
     *         the list will be empty if the user has no orders
     */
    List<Order> findByUserId(UUID userId);

    /**
     * Finds all orders matching the given status that were created before
     * the specified timestamp.
     *
     * Typically used by scheduled jobs to identify stale orders —
     * for example, pending orders that have not been paid within a
     * configured time limit so they can be cancelled or removed.
     *
     * @param status the order status to filter by (e.g., {@link OrderStatus#PENDING})
     * @param before the cutoff timestamp; only orders created before this instant are returned
     * @return a list of orders matching the status and creation time criteria
     */
    List<Order> findByStatusAndCreatedAtBefore(OrderStatus status, Instant before);
}
