package com.cartcloud.cartcloud.service.payment.strategy;

import com.cartcloud.cartcloud.model.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CardPaymentStrategy implements PaymentStrategy {

    @Override
    public String getMethod() {
        return "CARD";
    }

    @Override
    public String pay(Order order) {
        return "CARD-" + UUID.randomUUID();
    }
}
