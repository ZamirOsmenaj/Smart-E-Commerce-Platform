package com.example.ecommerce.repository;

import com.example.ecommerce.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for mapping {@link Order} entities.
 *
 * <p>
 *  Extends {@link JpaRepository} to obtain standard CRUD operations,
 *  paging, and sorting for {@link Order} instances.
 * </p>
 */
public interface OrderRepository extends JpaRepository<Order, UUID> {
    /**
     * Retrieves all orders associated with a specific user.
     *
     * @param userId the unique identifier of the user
     *
     * @return a list of {@link Order} instances belonging to the user;
     *         the list will be empty if the user has no orders
     */
    List<Order> findByUserId(UUID userId);
}
