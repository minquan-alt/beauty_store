package com.beautystore.adeline.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "supplier")
@NoArgsConstructor
public class Supplier {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "supplier_id")
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(name = "contact_info", columnDefinition = "CLOB")
  private String contactInfo;

  @OneToMany(mappedBy = "supplier")
  private List<Product> products = new ArrayList<>();
}