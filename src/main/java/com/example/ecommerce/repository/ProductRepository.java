package com.example.ecommerce.repository;

import com.example.ecommerce.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository interface for mapping {@link Product} entities.
 *
 * Extends {@link JpaRepository} to inherit standard CRUD operations.
 */
public interface ProductRepository extends JpaRepository<Product, UUID> {
    //EMPTY
}
