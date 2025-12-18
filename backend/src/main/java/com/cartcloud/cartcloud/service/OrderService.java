package com.cartcloud.cartcloud.service;

import com.cartcloud.cartcloud.model.*;
import com.cartcloud.cartcloud.repository.OrderItemRepository;
import com.cartcloud.cartcloud.repository.OrderRepository;
import com.cartcloud.cartcloud.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final InventoryService inventoryService;
    private final PaymentService paymentService;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        UserRepository userRepository,
                        CartService cartService,
                        InventoryService inventoryService,
                        PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.cartService = cartService;
        this.inventoryService = inventoryService;
        this.paymentService = paymentService;
    }

    @Transactional
    public Order checkout(Long userId, String paymentMethod) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartService.getCartForUser(userId);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

       
        for (CartItem item : cart.getItems()) {
            if (!inventoryService.hasSufficientStock(item.getProduct(), item.getQuantity())) {
                throw new RuntimeException("Not enough stock for product: " + item.getProduct().getName());
            }
        }

        
        Order order = new Order();
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setOrderItems(new ArrayList<OrderItem>());


        BigDecimal total = BigDecimal.ZERO;

        order = orderRepository.save(order);

        for (CartItem cartItem : cart.getItems()) {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(cartItem.getProduct());
            oi.setQuantity(cartItem.getQuantity());
            oi.setUnitPrice(cartItem.getProduct().getPrice());

            BigDecimal lineTotal = cartItem.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            oi.setLineTotal(lineTotal);
            total = total.add(lineTotal);

            orderItemRepository.save(oi);
            order.getOrderItems().add(oi);

            inventoryService.decreaseStock(cartItem.getProduct(), cartItem.getQuantity());
        }

        order.setTotalAmount(total);
        order = orderRepository.save(order);

       
    Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());  
        payment.setPaymentMethod(paymentMethod);     
        payment.setStatus("PENDING");   

        payment.getPaymentMethod();


        order = orderRepository.save(order);

        cartService.clearCart(cart);

        return order;
    }

    public List<Order> getOrdersForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findByUser(user);
    }
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
}
}
