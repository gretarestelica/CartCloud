package com.cartcloud.cartcloud.service;

import com.cartcloud.cartcloud.model.*;
import com.cartcloud.cartcloud.repository.*;

import jakarta.transaction.Transaction;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.management.RuntimeErrorException;
import java.util.List;

@Service
public class OrderService {

   private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

  public OrderService(
          OrderRepository orderRepository,
          CartRepository cartRepository,
          UserRepository userRepository,
          CartService cartService

  ){
    this.orderRepository = orderRepository;
    this.cartRepository = cartRepository;
    this.userRepository = userRepository;
    this.cartService = cartService;
  }

  @Transactional
  public Order placeOrder(Long userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Cart cart = cartRepository.findByUser(user)
           .orElseThrow(() -> new RuntimeException("Cart not found"));
           
    if(cart.getItems().isEmpty()) {
      throw new RuntimeException("Cart is empty");
    }       

    Order order = new Order();
    order.setUser(user);
    order.setOrderDate(LocalDateTime.now());
    order.setStatus("CREATED");

   List<OrderItem> orderItems = new ArrayList<>();
    BigDecimal total = BigDecimal.ZERO;


    for (CartItem ci : cart.getItems()) {
      OrderItem oi = new OrderItem();
      oi.setOrder(order);
      oi.setProduct(ci.getProduct());
      oi.setQuantity(ci.getQuantity());
      oi.setPrice(ci.getProduct().getPrice());

      total = total.add(
               ci.getProduct(). getPrice()
                     .multiply(BigDecimal.valueOf(ci.getQuantity()))
      );

      orderItems.add(oi);
    }

    order.setItems(orderItems);
    order.setTotalPrice(total);

    Order savedOrder = orderRepository.save(order);

    cartService.cleanCart(cart);

    return savedOrder;
  }

  public List<Order> getOrderForUser(Long userId) {
    return orderRepository.findByUserUserId(userId);
  }
  
}
