package com.cartcloud.cartcloud.service;

import com.cartcloud.cartcloud.model.*;
import com.cartcloud.cartcloud.repository.UserRepository;
import com.cartcloud.cartcloud.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    public WishlistService(WishlistRepository wishlistRepository,
                           UserRepository userRepository,
                           CartService cartService) {
        this.wishlistRepository = wishlistRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
    }

    @Transactional
    public Wishlist createFromCart(Long userId, int ttlDays) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartService.getCartForUser(userId);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty, nothing to save as wishlist");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setCreatedAt(LocalDateTime.now());
        wishlist.setExpiresAt(LocalDateTime.now().plusDays(ttlDays));
        wishlist.setToken(generateToken());

        for (CartItem cartItem : cart.getItems()) {
            WishlistItem wi = new WishlistItem();
            wi.setWishlist(wishlist);
            wi.setProduct(cartItem.getProduct());
            wi.setQuantity(cartItem.getQuantity());
            wishlist.getItems().add(wi);
        }

        return wishlistRepository.save(wishlist);
    }

    public Wishlist getByToken(String token) {
        Wishlist wl = wishlistRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Wishlist not found"));

        if (wl.getExpiresAt() != null && wl.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Wishlist has expired");
        }
        return wl;
    }

    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}


