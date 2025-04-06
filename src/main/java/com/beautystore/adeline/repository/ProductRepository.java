package com.beautystore.adeline.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beautystore.adeline.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{
    boolean existsByIdAndSupplierId(Long productId, Long supplierId);
}
