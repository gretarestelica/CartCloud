package com.cartcloud.cartcloud.controller;

import com.cartcloud.cartcloud.controller.dto.PayOrderRequest;
import com.cartcloud.cartcloud.model.Payment;
import com.cartcloud.cartcloud.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<Payment> payOrder(@Valid @RequestBody PayOrderRequest request) {
        Payment payment = paymentService.payOrder(request.orderId(), request.method());
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Payment> getPaymentByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getByOrderId(orderId));
    }
}
