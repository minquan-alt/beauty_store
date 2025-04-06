package com.beautystore.adeline.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beautystore.adeline.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{
    
}
