package com.example.ecommerce.domain;

import com.example.ecommerce.domain.enums.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a customer order.
 *
 * <p>
 * An order is associated with a specific user and contains multiple
 * {@link OrderItem} entries.
 * </p>
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Order {

    /**
     * The unique identifier of the order.
     */
    @Id
    @GeneratedValue
    private UUID id;

    /**
     * The identifier of the user who placed the order.
     * Cannot be null.
     */
    @Column(nullable = false)
    private UUID userId;

    /**
     * The total cost of the order.
     * Cannot be null.
     */
    @Column(nullable = false)
    private BigDecimal total;

    /**
     * The current status of the order.
     * Defaults to {@link OrderStatus#PENDING}.
     */
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    /**
     * The timestamp when the order was created.
     */
    private Instant createdAt;

    /**
     * The list of the items included in the order.
     * Each {@link OrderItem} is associated with this order.
     *
     * <p>
     * Cascade operations ensure that when an order is persisted or removed,
     * its items are handled accordingly.
     * </p>
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

}
