package com.beautystore.adeline.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.beautystore.adeline.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findById(Long id);

    @Query("SELECT p FROM Product p WHERE " +
            "(:keyword IS NULL OR LOWER(p.name) LIKE :keyword) AND " +
            "p.price >= :minPrice AND p.price <= :maxPrice AND " +
            "((:categories) IS NULL OR LOWER(p.category.name) IN (:categories))")
    Page<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("categories") List<String> categories,
            Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
            "p.price >= :minPrice AND p.price <= :maxPrice")
    Page<Product> searchByPriceOnly(@Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);
}
