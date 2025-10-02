package com.example.ecommerce.service;

import com.example.ecommerce.decorator.EcommerceNotificationService;
import com.example.ecommerce.domain.Inventory;
import com.example.ecommerce.domain.Product;
import com.example.ecommerce.factory.InventoryFactory;
import com.example.ecommerce.repository.InventoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service responsible for managing product inventory,
 * including reserving and releasing stock.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final EcommerceNotificationService notificationService;
    
    @Value("${inventory.low-stock-threshold:10}")
    private int lowStockThreshold;

    public Inventory findById(UUID productId) {
        return inventoryRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("No inventory for product " + productId));
    }

    @Transactional
    public void createInventory(UUID productId, int initialStock) {
        Inventory inventory = InventoryFactory.createInventoryForProduct(productId, initialStock);
        inventoryRepository.save(inventory);
    }

    @Transactional
    public void deleteInventoryById(UUID productId) {
        inventoryRepository.deleteById(productId);
    }

    /**
     * Reserves a specified quantity of stock for a product.
     * Sends low inventory alert if stock falls below threshold.
     *
     * @param productId the ID of the product
     * @param quantity  the quantity to reserve
     *
     * @throws RuntimeException if no inventory exists for the product
     *                          or if available stock is insufficient
     */
    @Transactional
    public void reserveStock(UUID productId, int quantity) {
        Inventory inv = findById(productId);
        if (inv.getAvailable() < quantity) {
             throw new RuntimeException("Insufficient stock");
         }

         int newAvailable = inv.getAvailable() - quantity;
         inv.setAvailable(newAvailable);
         inventoryRepository.save(inv);
         
         // Check if we need to send low inventory alert
         if (newAvailable <= lowStockThreshold) {
             Product product = productRepository.findById(productId).orElse(null);
             if (product != null) {
                 log.warn("Low inventory detected for product {} - Current stock: {}", 
                     product.getName(), newAvailable);
                 notificationService.sendLowInventoryAlert(product, newAvailable, lowStockThreshold);
             }
         }
    }

    /**
     * Releases a specified quantity of stock for a product.
     *
     * @param productId the ID of the product
     * @param quantity  the quantity to release back into inventory
     *
     * @throws RuntimeException if no inventory exists for the product
     */
    @Transactional
    public void releaseStock(UUID productId, int quantity) {
        Inventory inv = findById(productId);
        inv.setAvailable(inv.getAvailable() + quantity);
        inventoryRepository.save(inv);
    }
}
