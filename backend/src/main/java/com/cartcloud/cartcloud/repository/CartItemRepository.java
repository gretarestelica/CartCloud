package com.cartcloud.cartcloud.repository;

import com.cartcloud.cartcloud.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
  
}
