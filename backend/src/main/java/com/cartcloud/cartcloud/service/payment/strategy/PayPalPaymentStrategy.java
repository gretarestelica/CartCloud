package com.cartcloud.cartcloud.service.payment.strategy;

import com.cartcloud.cartcloud.model.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PayPalPaymentStrategy implements PaymentStrategy {

    @Override
    public String getMethod() {
        return "PAYPAL";
    }

    @Override
    public String pay(Order order) {
        return "PAYPAL-" + UUID.randomUUID();
    }
}
