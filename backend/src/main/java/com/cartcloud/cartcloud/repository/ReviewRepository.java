package com.cartcloud.cartcloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cartcloud.cartcloud.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
  
}
