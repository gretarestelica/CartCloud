package com.cartcloud.cartcloud.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long addressID;

  private String street;
  private String city;
  private String state;
  private String country;
  private String postalCode;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;
}