package com.example.ecommerce.repository;

import com.example.ecommerce.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for mapping {@link Inventory} entities.
 *
 * Extends {@link JpaRepository} to acquire standard CRUD operations.
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    //EMPTY
}
