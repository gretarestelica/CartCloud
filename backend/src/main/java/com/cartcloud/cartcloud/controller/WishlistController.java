package com.cartcloud.cartcloud.controller;

import com.cartcloud.cartcloud.model.Wishlist;
import com.cartcloud.cartcloud.service.WishlistService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlists")
@CrossOrigin(origins = "*")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @PostMapping("/from-cart")
    public Map<String, Object> createFromCart(@RequestParam Long userId,
                                              @RequestParam(name = "days", defaultValue = "14") int days) {
        Wishlist wishlist = wishlistService.createFromCart(userId, days);
        Map<String, Object> resp = new HashMap<>();
        resp.put("token", wishlist.getToken());
        resp.put("expiresAt", wishlist.getExpiresAt());
        return resp;
    }

    @GetMapping("/{token}")
    public Wishlist getByToken(@PathVariable String token) {
        return wishlistService.getByToken(token);
    }
}


