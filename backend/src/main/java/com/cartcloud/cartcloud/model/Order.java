package com.cartcloud.cartcloud.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long orderId;

   private LocalDateTime orderDate;

   private BigDecimal totalAmount;

   private String status;

   @ManyToOne
   @JoinColumn(name = "user_id")
   private User user;

   @ManyToOne
   @JoinColumn(name = "shipping_address_id")
   private Address shippingAddress;

   @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
   private List<OrderItem> orderItems;

   @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
   private Payment payment;

}