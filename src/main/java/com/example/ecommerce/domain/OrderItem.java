package com.example.ecommerce.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entity representing an item within an order.
 *
 * <p>
 * Each item links a product to an order and specifies the
 * quantity and price at the time of purchase.
 * </p>
 */
@Entity
@Table(name = "order_items")
@Data @Builder
public class OrderItem {

    /**
     * The unique identifier of the order item.
     */
    @Id
    @GeneratedValue
    private UUID id;

    /**
     * The order to which this item belongs.
     * Cannot be null.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * The identifier of the product being ordered.
     * Cannot be null.
     */
    @Column(nullable = false)
    private UUID productId;

    /**
     * The quantity of the product being ordered.
     * Cannot be null.
     */
    @Column(nullable = false)
    private int quantity;

    /**
     * The price of the product at the time of ordering.
     * Cannot be null.
     */
    @Column(nullable = false)
    private BigDecimal price;

}
