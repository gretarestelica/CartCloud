package com.cartcloud.cartcloud.repository;

import com.cartcloud.cartcloud.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
  
}
