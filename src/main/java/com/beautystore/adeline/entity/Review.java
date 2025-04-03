package com.beautystore.adeline.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "review")
@NoArgsConstructor
public class Review {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "review_id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  @Column(nullable = false)
  private Integer rating;

  @Column(name = "review_comment", columnDefinition = "CLOB")
  private String comment;

  @Column(name = "review_date")
  private LocalDateTime reviewDate = LocalDateTime.now();
}