package com.beautystore.adeline.repository;

import java.util.Optional;

import com.beautystore.adeline.entity.Inventory;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long>{
    public Optional<Inventory> findByProductId(long id);
}
