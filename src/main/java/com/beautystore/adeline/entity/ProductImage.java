package com.beautystore.adeline.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "product_image")
@NoArgsConstructor
public class ProductImage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "image_id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  @Column(name = "image_url", nullable = false, length = 4000)
  private String imageUrl;
}