package com.cartcloud.cartcloud.repository;

import com.cartcloud.cartcloud.model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
}


