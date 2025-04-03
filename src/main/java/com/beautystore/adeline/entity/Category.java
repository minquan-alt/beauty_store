package com.beautystore.adeline.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "category")
@NoArgsConstructor
public class Category {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "category_id")
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @OneToMany(mappedBy = "category")
  private List<Product> products = new ArrayList<>();
}