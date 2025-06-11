package com.beautystore.adeline.repository;

import java.util.List;
import java.util.Optional;

import com.beautystore.adeline.entity.PurchaseOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    @Query("""
            SELECT po
            FROM PurchaseOrder po
            JOIN po.orderDetails pod
            WHERE pod.product.id IN (
                SELECT od.product.id
                FROM OrderDetail od
                WHERE od.order.id = :orderId
            )
        """)
    List<PurchaseOrder> findByOrderId(Long orderId);

    @Query("""
            SELECT DISTINCT po
            FROM PurchaseOrder po
            JOIN po.orderDetails pod
            WHERE pod.product.id IN (
                SELECT od.product.id
                FROM OrderDetail od
                WHERE od.order.id = :orderId
            )
            AND po.status = :status
        """)
    List<PurchaseOrder> findByOrderIdAndStatus(Long orderId, PurchaseOrder.Status status);
}