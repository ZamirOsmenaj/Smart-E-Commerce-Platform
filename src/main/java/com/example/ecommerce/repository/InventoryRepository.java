package com.example.ecommerce.repository;

import com.example.ecommerce.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository interface for mapping {@link Inventory} entities.
 * <p>
 * Extends {@link JpaRepository} to acquire standard CRUD operations.
 */
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    //EMPTY
}
