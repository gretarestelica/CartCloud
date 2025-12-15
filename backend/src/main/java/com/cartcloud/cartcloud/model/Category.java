package com.cartcloud.cartcloud.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long categoryId;

  @Column(nullable = false, unique = true)
  private String name;

  private String description;

  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
  private List<Product>products;
}