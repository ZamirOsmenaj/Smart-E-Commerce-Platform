package com.example.ecommerce.service;

import com.example.ecommerce.domain.Inventory;
import com.example.ecommerce.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service responsible for managing product inventory,
 * including reserving and releasing stock.
 */
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    /**
     * Reserves a specified quantity of stock for a product.
     *
     * @param productId the ID of the product
     * @param quantity  the quantity to reserve
     *
     * @throws RuntimeException if no inventory exists for the product
     *                          or if available stock is insufficient
     */
    public void reserveStock(UUID productId, int quantity) {
         Inventory inv = inventoryRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("No inventory for product " + productId));

         if (inv.getAvailable() < quantity) {
             throw new RuntimeException("Insufficient stock");
         }

         inv.setAvailable(inv.getAvailable() - quantity);
         inventoryRepository.save(inv);
    }

    /**
     * Releases a specified quantity of stock for a product.
     *
     * @param productId the ID of the product
     * @param quantity  the quantity to release back into inventory
     *
     * @throws RuntimeException if no inventory exists for the product
     */
    public void releaseStock(UUID productId, int quantity) {
        Inventory inv = inventoryRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("No inventory for product " + productId));
        inv.setAvailable(inv.getAvailable() - quantity);
        inventoryRepository.save(inv);
    }

}
