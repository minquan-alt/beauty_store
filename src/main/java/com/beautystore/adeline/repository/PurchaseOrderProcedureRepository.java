package com.beautystore.adeline.repository;

import java.math.BigDecimal;
import java.util.List;


import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Repository;
@Repository
public class PurchaseOrderProcedureRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Long processPurchaseOrder(
            List<Long> productIds,
            List<Integer> quantities,
            List<BigDecimal> unitPrices
    ) {
        try {
            // Tạo Purchase Order trước và lấy ID
            Long purchaseOrderId = createPurchaseOrder();
            
            // Insert từng item vào purchase order details
            for (int i = 0; i < productIds.size(); i++) {
                insertPurchaseOrderDetail(
                    purchaseOrderId,
                    productIds.get(i),
                    quantities.get(i),
                    unitPrices.get(i)
                );
            }
            
            return purchaseOrderId;
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi gọi stored procedure: " + e.getMessage(), e);
        }
    }
    
    private Long createPurchaseOrder() {
        try {
            // Gọi procedure tạo Purchase Order và trả về ID
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("BEAUTY_STORE.create_purchase_order");
            query.registerStoredProcedureParameter("p_order_id", Long.class, ParameterMode.OUT);
            
            query.execute();
            
            Object result = query.getOutputParameterValue("p_order_id");
            if (result == null) {
                throw new RuntimeException("Procedure create_purchase_order không trả về order ID");
            }
            
            return ((Number) result).longValue();
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo purchase order: " + e.getMessage(), e);
        }
    }
    
    private void insertPurchaseOrderDetail(Long purchaseOrderId, Long productId, Integer quantity, BigDecimal unitPrice) {
        try {
            // Gọi procedure insert detail
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("BEAUTY_STORE.insert_purchase_order_detail");
            query.registerStoredProcedureParameter("p_purchase_order_id", Long.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_product_id", Long.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_quantity", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_unit_price", BigDecimal.class, ParameterMode.IN);
            
            query.setParameter("p_purchase_order_id", purchaseOrderId);
            query.setParameter("p_product_id", productId);
            query.setParameter("p_quantity", quantity);
            query.setParameter("p_unit_price", unitPrice);
            
            query.execute();
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi insert purchase order detail: " + e.getMessage(), e);
        }
    }
}