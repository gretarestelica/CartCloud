package com.cartcloud.cartcloud.repository;

import com.cartcloud.cartcloud.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long>{
  
}
