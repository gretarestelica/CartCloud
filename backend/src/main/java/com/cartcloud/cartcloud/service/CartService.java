package com.cartcloud.cartcloud.service;

import com.cartcloud.cartcloud.model.*;
import com.cartcloud.cartcloud.repository.CartItemRepository;
import com.cartcloud.cartcloud.repository.CartRepository;
import com.cartcloud.cartcloud.repository.ProductRepository;
import com.cartcloud.cartcloud.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;


@Service
public class CartService {

  private final CartRepository cartRepository;
  private final CartItemRepository cartItemRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;

  public CartService(CartRepository cartRepository,CartItemRepository cartItemRepository,ProductRepository productRepository, UserRepository userRepository ){
    this.cartRepository = cartRepository;
    this.cartItemRepository = cartItemRepository;
    this.productRepository = productRepository;
    this.userRepository = userRepository;
  }

  protected Cart getOrCreateCartForUser(User user){
    return cartRepository.findByUser(user)
            .orElseGet(() -> {
              Cart cart = new Cart();
              cart.setUser(user);
              return cartRepository.save(cart);
            });
  }

  @Transactional
  public Cart addToCart(Long userId, Long productId, int quantity) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("USer not found"));

            Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

            Cart cart = getOrCreateCartForUser(user);

            Optional<CartItem> existingItemOpt = cart.getItems()
                    .stream()
                    .filter(ci -> ci.getProduct().getProductId().equals(productId))
                    .findFirst();
            if(existingItemOpt.isPresent()){
              CartItem existing =existingItemOpt.get();
              existing.setQuantity(existing.getQuantity() + quantity);
              cartItemRepository.save(existing);
            }else{
              CartItem newItem = new CartItem();
              newItem.setCart(cart);
              newItem.setProduct(product);
              newItem.setQuantity(quantity);
              cartItemRepository.save(newItem);
              cart.getItems().add(newItem);
            }

            return cartRepository.save(cart);
  }
  @Transactional
  public Cart removeFromCart(Long userId, Long cartItemId){
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Cart not found"));
            
    cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
    cartItemRepository.deleteById(cartItemId);
    
    
    return cartRepository.save(cart);
  }

  public Cart getCartForUser(Long userId){
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("USer not found"));

    return cartRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Cart not found"));     
  }
  @Transactional
  public void cleanCart(Cart cart){
    cartItemRepository.deleteAll(cart.getItems());
    cart.getItems().clear();
    cartRepository.save(cart);
  }


  
}
