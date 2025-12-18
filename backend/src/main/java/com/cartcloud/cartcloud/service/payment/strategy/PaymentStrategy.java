package com.cartcloud.cartcloud.service.payment.strategy;

import com.cartcloud.cartcloud.model.Order;

public interface PaymentStrategy {
    String getMethod();     
    String pay(Order order); 
}
