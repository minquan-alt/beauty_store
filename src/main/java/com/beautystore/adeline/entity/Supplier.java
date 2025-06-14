package com.beautystore.adeline.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

  @JsonBackReference
  @ToString.Exclude
  @OneToMany(mappedBy = "supplier")
  private List<Product> products = new ArrayList<>();
}
