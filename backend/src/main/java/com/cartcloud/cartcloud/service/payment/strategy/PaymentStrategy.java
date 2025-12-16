package com.cartcloud.cartcloud.service.payment.strategy;

import com.cartcloud.cartcloud.model.Payment;

public interface PaymentStrategy {
    void pay(Payment payment);
}
