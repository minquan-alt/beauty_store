package com.beautystore.adeline.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beautystore.adeline.entity.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long>{
    
}
