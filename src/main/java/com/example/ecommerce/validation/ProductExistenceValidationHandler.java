package com.example.ecommerce.validation;

import com.example.ecommerce.dto.CreateOrderRequestDTO;
import com.example.ecommerce.proxy.ProductServiceContract;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validates that all products in the order actually exist.
 * This is a REAL validation that prevents orders with non-existent products.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductExistenceValidationHandler extends OrderValidationHandler {
    
    private final ProductServiceContract productService;
    
    @Override
    protected ValidationResult doValidate(CreateOrderRequestDTO request) {
        log.debug("Validating product existence for {} items", request.getItems().size());
        
        for (int i = 0; i < request.getItems().size(); i++) {
            var item = request.getItems().get(i);
            
            try {
                // Check if product exists
                var product = productService.findById(item.getProductId());
                log.debug("Product {} exists: {}", item.getProductId(), product.getName());
                
            } catch (RuntimeException e) {
                return ValidationResult.failure(getValidationName(), 
                    String.format("Item %d: Product with ID %s does not exist", 
                            i + 1, item.getProductId()));
            }
        }
        
        log.debug("Product existence validation passed for all {} items", request.getItems().size());
        return ValidationResult.success(getValidationName());
    }
    
    @Override
    protected String getValidationName() {
        return "Product Existence Validation";
    }
}