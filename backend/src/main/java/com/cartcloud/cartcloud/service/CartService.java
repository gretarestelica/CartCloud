package com.cartcloud.cartcloud.service;

import com.cartcloud.cartcloud.model.*;
import com.cartcloud.cartcloud.repository.CartItemRepository;
import com.cartcloud.cartcloud.repository.CartRepository;
import com.cartcloud.cartcloud.repository.ProductRepository;
import com.cartcloud.cartcloud.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    protected Cart getOrCreateCartForUser(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);

                    
                    if (cart.getItems() == null) {
                        cart.setItems(new ArrayList<>());
                    }

                    cart.setTotalPrice(BigDecimal.ZERO);
                    return cartRepository.save(cart);
                });
    }

    @Transactional
    public Cart addToCart(Long userId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be > 0");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getPrice() == null) {
            throw new RuntimeException("Product price is missing");
        }

        Cart cart = getOrCreateCartForUser(user);

        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(ci -> ci.getProduct() != null
                        && ci.getProduct().getProductId() != null
                        && ci.getProduct().getProductId().equals(productId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existing = existingItemOpt.get();
            int newQty = existing.getQuantity() + quantity;
            existing.setQuantity(newQty);

            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(newQty));
            existing.setLineTotal(lineTotal);

            cartItemRepository.save(existing);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);

            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
            newItem.setLineTotal(lineTotal);

            cartItemRepository.save(newItem);
            cart.getItems().add(newItem);
        }

        recalculateTotals(cart);
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeFromCart(Long userId, Long cartItemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems() == null) {
            return cart;
        }

        
        cart.getItems().removeIf(item ->item.getId().equals(cartItemId));
        cartItemRepository.deleteById(cartItemId);

        recalculateTotals(cart);
        return cartRepository.save(cart);
    }

    public Cart getCartForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
    }

    @Transactional
    public void clearCart(Cart cart) {
        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            cartItemRepository.deleteAll(cart.getItems());
            cart.getItems().clear();
        }
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    private void recalculateTotals(Cart cart) {
        BigDecimal total = BigDecimal.ZERO;

        if (cart.getItems() == null) {
            cart.setTotalPrice(BigDecimal.ZERO);
            return;
        }

        for (CartItem item : cart.getItems()) {
            if (item.getProduct() == null || item.getProduct().getPrice() == null) {
                item.setLineTotal(BigDecimal.ZERO);
                continue;
            }

            BigDecimal line = item.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            item.setLineTotal(line);
            total = total.add(line);
        }

        cart.setTotalPrice(total);
    }
}
