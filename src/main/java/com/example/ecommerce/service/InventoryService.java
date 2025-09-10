package com.example.ecommerce.service;

import com.example.ecommerce.domain.Inventory;
import com.example.ecommerce.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public void reserveStock(UUID productId, int quantity) {
         Inventory inv = inventoryRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("No inventory for product " + productId));

         if (inv.getAvailable() < quantity) {
             throw new RuntimeException("Insufficient stock");
         }

         inv.setAvailable(inv.getAvailable() - quantity);
         inventoryRepository.save(inv);
    }

    public void releaseStock(UUID productId, int quantity) {
        Inventory inv = inventoryRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("No inventory for product " + productId));
        inv.setAvailable(inv.getAvailable() - quantity);
        inventoryRepository.save(inv);
    }


}
