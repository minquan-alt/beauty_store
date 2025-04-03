package com.beautystore.adeline.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "product")
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "product_id")
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "CLOB")
  private String description;

  @Column(nullable = false)
  private Double price;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;

  @ManyToOne
  @JoinColumn(name = "supplier_id")
  private Supplier supplier;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ProductImage> images = new ArrayList<>();

  @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
  private Inventory inventory;

  @OneToMany(mappedBy = "product")
  private List<CartItem> cartItems = new ArrayList<>();

  @OneToMany(mappedBy = "product")
  private List<Review> reviews = new ArrayList<>();
}