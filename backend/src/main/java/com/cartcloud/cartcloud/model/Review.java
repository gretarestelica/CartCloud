package com.cartcloud.cartcloud.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor

public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    private int rating;
    private String comment;


    @ManyToOne
    @JoinColumn(name = "user_id")  
    private User user;

    @ManyToOne 
    @JoinColumn(name = "product_id")
    private Product product;

    private LocalDateTime createdAt;
}