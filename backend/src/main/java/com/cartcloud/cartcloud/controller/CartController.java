package com.cartcloud.cartcloud.controller;

import com.cartcloud.cartcloud.model.Cart;
import com.cartcloud.cartcloud.service.CartService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public Cart getCart(@PathVariable Long userId) {
        return cartService.getCartForUser(userId);
    }

    @PostMapping("/{userId}/items")
    public Cart addItem(@PathVariable Long userId,
                        @RequestParam Long productId,
                        @RequestParam(defaultValue = "1") int quantity) {
        return cartService.addToCart(userId, productId, quantity);
    }

    @DeleteMapping("/{userId}/items/{cartItemId}")
    public Cart removeItem(@PathVariable Long userId, @PathVariable Long cartItemId) {
        return cartService.removeFromCart(userId, cartItemId);
    }

    @DeleteMapping("/{userId}/clear")
    public void clear(@PathVariable Long userId) {
        Cart cart = cartService.getCartForUser(userId);
        cartService.clearCart(cart);
    }
}

