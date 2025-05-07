package com.beautystore.adeline.repository;

import java.util.List;
import java.util.Optional;

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
      "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
      "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
      "((:categories) IS NULL OR LOWER(p.category.name) IN (:categories))")
  List<Product> searchProducts(
      @Param("keyword") String keyword,
      @Param("minPrice") Double minPrice,
      @Param("maxPrice") Double maxPrice,
      @Param("categories") List<String> categories);
}
