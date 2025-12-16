package com.cartcloud.cartcloud.service.payment.strategy;

import com.cartcloud.cartcloud.model.Payment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component("PAYPAL")
public class PayPalPaymentStrategy implements PaymentStrategy {

    @Override
    public void pay(Payment payment) {
        payment.setStatus("PAID"); 
        payment.setTransactionId("PAYPAL-" + UUID.randomUUID());
        payment.setCreatedAt(LocalDateTime.now());
    }
}
