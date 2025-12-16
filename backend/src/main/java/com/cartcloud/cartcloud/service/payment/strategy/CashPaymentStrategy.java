package com.cartcloud.cartcloud.service.payment.strategy;

import com.cartcloud.cartcloud.model.Payment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component("CASH")
public class CashPaymentStrategy implements PaymentStrategy {

    @Override
    public void pay(Payment payment) {
        payment.setStatus("PENDING"); 
        payment.setTransactionId("CASH-" + UUID.randomUUID());
        payment.setCreatedAt(LocalDateTime.now());
    }
}
