package com.beautystore.adeline.services;

import java.sql.CallableStatement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beautystore.adeline.dto.request.PurchaseOrderRequest;
import com.beautystore.adeline.dto.response.PurchaseOrderResponse;
import com.beautystore.adeline.entity.PurchaseOrder;
import com.beautystore.adeline.repository.ProductRepository;
import com.beautystore.adeline.repository.PurchaseOrderRepository;
import com.beautystore.adeline.repository.SupplierRepository;

import java.sql.Array;
import java.sql.Struct;

import oracle.jdbc.OracleConnection;
import org.hibernate.Session;

import jakarta.persistence.EntityManager;

@Service
public class PurchaseOrderService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    public PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request) {
        // B1: Chuẩn bị OracleConnection
        Session session = entityManager.unwrap(Session.class);
        OracleConnection connection = session
                .doReturningWork(c -> c.unwrap(OracleConnection.class));

        try {
            // B2: Chuẩn bị ARRAY Oracle
            Struct[] itemStructs = new Struct[request.getItems().size()];
            for (int i = 0; i < request.getItems().size(); i++) {
                PurchaseOrderRequest.PurchaseItem item = request.getItems().get(i);
                Object[] attrs = new Object[] {
                        item.getProductId(),
                        item.getQuantity(),
                        item.getUnitPrice()
                };
                itemStructs[i] = connection.createStruct("PURCHASE_ITEM_TYPE", attrs);
            }

            Array itemsArray = connection.createOracleArray("PURCHASE_ITEM_TABLE_TYPE", itemStructs);

            // B3: Gọi Stored Procedure
            CallableStatement stmt = connection.prepareCall("{ call process_purchase_order(?, ?) }");
            stmt.setLong(1, request.getSupplierId());
            stmt.setArray(2, itemsArray);
            stmt.execute();
            stmt.close();

            // B4: Lấy purchase order mới nhất theo supplier_id
            PurchaseOrder order = purchaseOrderRepository
                    .findTopBySupplierIdOrderByIdDesc(request.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Purchase Order sau khi tạo"));

            // B5: Trả về DTO
            return PurchaseOrderResponse.builder()
                    .purchaseOrderId(order.getId())
                    .supplierName(order.getSupplier().getName())
                    .orderDate(order.getOrderDate())
                    .status(order.getStatus().name())
                    .items(order.getOrderDetails().stream()
                            .map(detail -> PurchaseOrderResponse.PurchaseOrderDetailResponse.builder()
                                    .productName(detail.getProduct().getName())
                                    .quantity(detail.getQuantity())
                                    .unitPrice(detail.getUnitPrice())
                                    .build())
                            .toList())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi gọi stored procedure: " + e.getMessage(), e);
        }
    }
}