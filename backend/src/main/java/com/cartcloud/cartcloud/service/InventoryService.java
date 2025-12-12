package com.cartcloud.cartcloud.service;

import com.cartcloud.cartcloud.model.Inventory;
import com.cartcloud.cartcloud.model.Product;
import com.cartcloud.cartcloud.repository.InventoryRepository;
import com.cartcloud.cartcloud.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public InventoryService(InventoryRepository inventoryRepository,
                            ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    public Optional<Inventory> getInventoryForProduct(Product product) {
        return inventoryRepository.findByProduct(product);
    }

    public Optional<Inventory> getInventoryForProduct(Long productId) {
        return productRepository.findById(productId)
                .flatMap(inventoryRepository::findByProduct);
    }

    public boolean hasSufficientStock(Product product, int requestedQty) {
        return inventoryRepository.findByProduct(product)
                .map(inv -> inv.getQuantity() >= requestedQty)
                .orElse(false);
    }

    public void decreaseStock(Product product, int quantity) {
        Inventory inv = inventoryRepository.findByProduct(product)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product"));

        if (inv.getQuantity() < quantity) {
            throw new RuntimeException("Not enough stock");
        }

        inv.setQuantity(inv.getQuantity() - quantity);
        inventoryRepository.save(inv);
    }

    public void increaseStock(Product product, int quantity) {
        Inventory inv = inventoryRepository.findByProduct(product)
                .orElseGet(() -> {
                    Inventory i = new Inventory();
                    i.setProduct(product);
                    i.setQuantity(0);
                    return i;
                });

        inv.setQuantity(inv.getQuantity() + quantity);
        inventoryRepository.save(inv);
    }
}
