package com.example.ecommerce.domain.enums;

import com.example.ecommerce.domain.Order;

/**
 * Enumeration representing the possible states of an {@link Order}.
 */
public enum OrderStatus {

    /**
     * The order has been created but not yet paid.
     */
    PENDING,

    /**
     * The order has been paid successfully.
     */
    PAID,

    /**
     * The order has been cancelled and will not be processed further.
     */
    CANCELLED

}
