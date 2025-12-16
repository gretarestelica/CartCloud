package com.cartcloud.cartcloud.repository;

import com.cartcloud.cartcloud.model.Inventory;
import com.cartcloud.cartcloud.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProduct(Product product);
    boolean existingByProduct(Product product);
}
