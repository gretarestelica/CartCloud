package com.cartcloud.cartcloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cartcloud.cartcloud.model.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
  
}
