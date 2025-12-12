package com.cartcloud.cartcloud.repository;

import com.cartcloud.cartcloud.model.Category;
import com.cartcloud.cartcloud.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
  
      List<Product> findByCategory(Category category);

}
