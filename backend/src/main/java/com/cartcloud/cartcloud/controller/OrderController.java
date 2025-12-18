package com.cartcloud.cartcloud.controller;

import com.cartcloud.cartcloud.model.Order;
import com.cartcloud.cartcloud.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public Order checkout(@RequestParam Long userId,
                          @RequestParam(defaultValue = "CASH_ON_DELIVERY") String paymentMethod) {
        return orderService.checkout(userId, paymentMethod);
    }

    @GetMapping("/user/{userId}")
    public List<Order> getOrdersForUser(@PathVariable Long userId) {
        return orderService.getOrdersForUser(userId);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
}

}

