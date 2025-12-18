package com.cartcloud.cartcloud.service.payment.strategy;

import com.cartcloud.cartcloud.model.Order;
import org.springframework.stereotype.Component;

@Component
public class CashPaymentStrategy implements PaymentStrategy {

    @Override
    public String getMethod() {
        return "CASH";
    }

    @Override
    public String pay(Order order) {
        return "CASH";
    }
}
