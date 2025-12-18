package com.cartcloud.cartcloud.service;

import com.cartcloud.cartcloud.model.Order;
import com.cartcloud.cartcloud.model.Payment;
import com.cartcloud.cartcloud.repository.OrderRepository;
import com.cartcloud.cartcloud.repository.PaymentRepository;
import com.cartcloud.cartcloud.service.payment.strategy.PaymentStrategy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    
    private final Map<String, PaymentStrategy> strategyByMethod;

    public PaymentService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            List<PaymentStrategy> strategies
    ) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;

        this.strategyByMethod = strategies.stream()
                .collect(Collectors.toMap(
                        s -> normalize(s.getMethod()),
                        Function.identity()
                ));
    }

    public Payment payOrder(Long orderId, String method) {
        String normalizedMethod = normalize(method);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

       
        if (order.getStatus() != null && order.getStatus().equalsIgnoreCase("PAID")) {
            throw new RuntimeException("Order is already PAID: " + orderId);
        }

        PaymentStrategy strategy = strategyByMethod.get(normalizedMethod);
        if (strategy == null) {
            throw new RuntimeException("Unsupported payment method: " + method + ". Allowed: " + strategyByMethod.keySet());
        }

       
        String transactionId = strategy.pay(order);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(normalizedMethod);
        payment.setStatus("SUCCESS"); 
        payment.setTransactionId(transactionId);
        payment.setCreatedAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        
        order.setStatus("PAID");
        orderRepository.save(order);

        return saved;
    }

    public Payment getByOrderId(Long orderId) {
        return paymentRepository.findByOrder_OrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for orderId: " + orderId));
    }

    private String normalize(String s) {
        if (s == null) return null;
        return s.trim().toUpperCase(Locale.ROOT);
    }
}
