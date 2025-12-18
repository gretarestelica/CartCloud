package com.cartcloud.cartcloud.repository;

import com.cartcloud.cartcloud.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface PaymentRepository extends JpaRepository<Payment, Long>{
  Optional<Payment> findByOrder_OrderId(Long orderId);

}
  

