package com.cartcloud.cartcloud.service.payment.strategy;

import com.cartcloud.cartcloud.model.Payment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component("CARD")
public class CardPaymentStrategy implements PaymentStrategy {

    @Override
    public void pay(Payment payment) {
        payment.setStatus("PAID"); 
        payment.setTransactionId("CARD-" + UUID.randomUUID());
        payment.setCreatedAt(LocalDateTime.now());
    }
}
