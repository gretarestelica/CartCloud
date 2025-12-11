package com.cartcloud.cartcloud.repository;

import com.cartcloud.cartcloud.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
  
}
