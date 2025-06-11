package com.beautystore.adeline.services;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Struct;
import java.util.List;
import java.util.stream.Collectors;

import com.beautystore.adeline.dto.request.PurchaseOrderRequest;
import com.beautystore.adeline.dto.request.PurchaseOrderUpdateRequest;
import com.beautystore.adeline.dto.response.PurchaseOrderDetailResponse;
import com.beautystore.adeline.dto.response.PurchaseOrderResponse;
import com.beautystore.adeline.entity.Product;
import com.beautystore.adeline.entity.PurchaseOrder;
import com.beautystore.adeline.entity.PurchaseOrder.Status;
import com.beautystore.adeline.entity.PurchaseOrderDetail;
import com.beautystore.adeline.exception.AppException;
import com.beautystore.adeline.exception.ErrorCode;
import com.beautystore.adeline.mapper.PurchaseOrderResponseMapper;
import com.beautystore.adeline.repository.ProductRepository;
import com.beautystore.adeline.repository.PurchaseOrderRepository;
import com.beautystore.adeline.repository.SupplierRepository;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import oracle.jdbc.OracleConnection;

@Service
public class PurchaseOrderService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private  PurchaseOrderResponseMapper purchaseOrderResponseMapper;



    @Autowired
    private ProductRepository productRepository;

    public PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request) {
        // B1: Lấy OracleConnection
        Session session = entityManager.unwrap(Session.class);
        OracleConnection connection = session
                .doReturningWork(c -> c.unwrap(OracleConnection.class));
    
        try {
            /*---------------------------------------------------------
             * B2: Chuẩn bị ARRAY Oracle (giữ nguyên logic cũ)
             *--------------------------------------------------------*/
            Struct[] itemStructs = new Struct[request.getItems().size()];
            for (int i = 0; i < request.getItems().size(); i++) {
                PurchaseOrderRequest.PurchaseItem item = request.getItems().get(i);
                Object[] attrs = {
                        item.getProductId(),
                        item.getQuantity(),
                        item.getUnitPrice()
                };
                itemStructs[i] = connection.createStruct("PURCHASE_ITEM_TYPE", attrs);
            }
    
            Array itemsArray =
                    connection.createOracleArray("PURCHASE_ITEM_TABLE_TYPE", itemStructs);
    
            /*---------------------------------------------------------
             * B3: Gọi stored procedure  (CHỈ CÒN 1 THAM SỐ)
             *--------------------------------------------------------*/
            CallableStatement stmt = connection.prepareCall("{ call process_purchase_order(?) }");
            stmt.setArray(1, itemsArray);          // <-- paramIndex = 1
            stmt.execute();
            stmt.close();
    
            /*---------------------------------------------------------
             * B4: Lấy đơn hàng vừa tạo
             *   (tìm đơn mới nhất theo ID giảm dần)
             *--------------------------------------------------------*/
            PurchaseOrder order = purchaseOrderRepository
                    .findTopByOrderByIdDesc()      // cần khai báo method này trong repository
                    .orElseThrow(() ->
                            new RuntimeException("Không tìm thấy Purchase Order sau khi tạo"));
    
            /*---------------------------------------------------------
             * B5: Trả về DTO
             *--------------------------------------------------------*/
            return PurchaseOrderResponse.builder()
                    .purchaseOrderId(order.getId())
                    .orderDate(order.getOrderDate())   // SYSDATE lấy từ DB
                    .status(order.getStatus().name())
                    .items(order.getOrderDetails().stream()
                            .map(detail -> PurchaseOrderDetailResponse.builder()
                                    .productId(detail.getProduct().getId())
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
    

    public int countPurchaseOrders(){
        return (int) purchaseOrderRepository.count();
    }

    public Page<PurchaseOrderResponse> getPurchaseOrdersPage(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<PurchaseOrder> purchaseOrderPage = purchaseOrderRepository.findAll(pageable);
        return purchaseOrderPage.map(purchaseOrderResponseMapper::toResponse);
    }

    public PurchaseOrderResponse updateOrderStatus(Long id, String status){
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PURCHASE_ORDER_NOT_EXISTED));

        if(purchaseOrder.getStatus() == Status.Completed){
            throw new AppException(ErrorCode.PURCHASE_ORDER_COMPLETED);
        }
        if(purchaseOrder.getStatus() == Status.Cancelled){
            throw new AppException(ErrorCode.PURCHASE_ORDER_CANCELLED);
        }
        purchaseOrder.setStatus(Status.valueOf(status)); 
        purchaseOrderRepository.save(purchaseOrder);

        return PurchaseOrderResponse.builder()
        .purchaseOrderId(purchaseOrder.getId())
        .orderDate(purchaseOrder.getOrderDate())
        .status(purchaseOrder.getStatus().name())
        .items(purchaseOrder.getOrderDetails().stream().map(detail ->
                PurchaseOrderDetailResponse.builder()
                        .productId(detail.getProduct().getId())
                        .productName(detail.getProduct().getName())
                        .quantity(detail.getQuantity())
                        .unitPrice(detail.getUnitPrice())
                        .build()
        ).toList())
        .build();
    }

    public PurchaseOrderResponse getPurchaseOrderById(Long id){
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
            .orElseThrow(() -> {
                return new AppException(ErrorCode.PURCHASE_ORDER_NOT_FOUND);
            });
        return purchaseOrderResponseMapper.toResponse(purchaseOrder);
    }

    @Transactional
    public PurchaseOrderResponse updatePurchaseOrder(PurchaseOrderUpdateRequest request, Long id){
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
            .orElseThrow(() -> {
                return new AppException(ErrorCode.PURCHASE_ORDER_NOT_FOUND);
            });
        if(purchaseOrder.getStatus() == Status.Completed){
            throw new AppException(ErrorCode.PURCHASE_ORDER_COMPLETED);
        }
        if(purchaseOrder.getStatus() == Status.Cancelled){
            throw new AppException(ErrorCode.PURCHASE_ORDER_CANCELLED);
        }


        if(request.getItems() == null){
            throw new AppException(ErrorCode.PURCHASE_ORDER_DETAIL_REQUIRED);
        }

        purchaseOrder.getOrderDetails().clear();

         List<PurchaseOrderDetail> newDetails = request.getItems().stream().map(item -> {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            PurchaseOrderDetail detail = new PurchaseOrderDetail();
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(item.getUnitPrice());
            detail.setPurchaseOrder(purchaseOrder); 
            return detail;
        }).collect(Collectors.toList());

    purchaseOrder.getOrderDetails().addAll(newDetails);
        
        PurchaseOrder updatedPurchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        return purchaseOrderResponseMapper.toResponse(updatedPurchaseOrder);
    }
}
