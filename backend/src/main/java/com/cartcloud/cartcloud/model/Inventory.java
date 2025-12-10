package com.cartcloud.cartcloud.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long inventoryId;

  private int stockQuantity;

  @OneToOne
  @JoinColumn(name = "product_id")
  private Product product;
}