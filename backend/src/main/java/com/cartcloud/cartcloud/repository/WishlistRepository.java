package com.cartcloud.cartcloud.repository;

import com.cartcloud.cartcloud.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByToken(String token);
}


