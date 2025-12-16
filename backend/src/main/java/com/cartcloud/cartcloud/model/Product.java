package com.cartcloud.cartcloud.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long ProductId;

  private String name;

  @Column(length = 1000)
  private String description;

  private BigDecimal price;

  private String imageUrl;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
  private List<Review> reviews;

  @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
  private Inventory inventory;
}