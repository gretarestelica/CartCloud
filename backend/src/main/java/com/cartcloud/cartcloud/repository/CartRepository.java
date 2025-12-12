package com.cartcloud.cartcloud.repository;

import com.cartcloud.cartcloud.model.Cart;
import com.cartcloud.cartcloud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CartRepository extends JpaRepository<Cart, Long> {
      Optional<Cart> findByUser(User user);
}
