package com.cartcloud.cartcloud.repository;

import com.cartcloud.cartcloud.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
  
}
