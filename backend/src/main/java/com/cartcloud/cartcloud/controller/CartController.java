package com.cartcloud.cartcloud.controller;

import com.cartcloud.cartcloud.controller.dto.AddToCartRequest;
import com.cartcloud.cartcloud.controller.dto.RemoveFromCartRequest;
import com.cartcloud.cartcloud.model.Cart;
import com.cartcloud.cartcloud.service.CartService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    
    @PostMapping("/add")
    public Cart addToCart(@Valid @RequestBody AddToCartRequest req) {
        return cartService.addToCart(req.userId(), req.productId(), req.quantity());
    }

    
    @DeleteMapping("/remove")
    public Cart removeFromCart(@Valid @RequestBody RemoveFromCartRequest req) {
        return cartService.removeFromCart(req.userId(), req.cartItemId());
    }

    
    @GetMapping("/{userId}")
    public Cart getCart(@PathVariable Long userId) {
        return cartService.getCartForUser(userId);
    }

    
    @PostMapping("/{userId}/clear")
    public void clearCart(@PathVariable Long userId) {
        Cart cart = cartService.getCartForUser(userId);
        cartService.clearCart(cart);
    }
}
