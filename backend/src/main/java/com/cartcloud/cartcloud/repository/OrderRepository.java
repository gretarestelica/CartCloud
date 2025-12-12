package com.cartcloud.cartcloud.repository;

import com.cartcloud.cartcloud.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>{
  List<Order> findByUserUserId(Long userId);
}
