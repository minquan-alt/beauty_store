package com.beautystore.adeline.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.beautystore.adeline.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
