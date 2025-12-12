package com.cartcloud.cartcloud.repository;

import com.cartcloud.cartcloud.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
