package com.beautystore.adeline.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.beautystore.adeline.entity.PurchaseOrder;


@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    Optional<PurchaseOrder> findTopBySupplierIdOrderByIdDesc(Long supplierId);
}
