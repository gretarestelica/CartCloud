package com.cartcloud.cartcloud.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart{

  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  private Long cartId;

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;

  private BigDecimal tottalPrice;

  @ManyToMany
  @JoinTable(
    name= "cart_products",
    joinColumns= @JoinColumn(name = "cart_id"),
    inverseJoinColumns= @JoinColumn(name = "product_id")
  )

  private List<Product> producrs;
}