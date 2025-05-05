package com.beautystore.adeline.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "inventory")
@NoArgsConstructor
public class Inventory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "inventory_id")
  private Long id;

  @OneToOne
  @JoinColumn(name = "product_id", unique = true)
  private Product product;

  @Column(name = "stock_quantity", nullable = false)
  private Integer stockQuantity = 0;
}