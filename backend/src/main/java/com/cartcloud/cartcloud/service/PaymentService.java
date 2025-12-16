package com.cartcloud.cartcloud.service;

import com.cartcloud.cartcloud.model.Payment;
import com.cartcloud.cartcloud.repository.PaymentRepository;
import com.cartcloud.cartcloud.service.payment.strategy.PaymentStrategy;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final Map<String, PaymentStrategy> strategies;

    public PaymentService(PaymentRepository paymentRepository,
                          Map<String, PaymentStrategy> strategies) {
        this.paymentRepository = paymentRepository;
        this.strategies = strategies;
    }

    public Payment processPayment(Payment payment) {
        if (payment.getPaymentMethod() == null) {
            throw new RuntimeException("Payment method is required");
        }

        PaymentStrategy strategy = strategies.get(payment.getPaymentMethod());

        if (strategy == null) {
            throw new RuntimeException("Unsupported payment method: " + payment.getPaymentMethod());
        }

        strategy.pay(payment);
        return paymentRepository.save(payment);
    }
}
