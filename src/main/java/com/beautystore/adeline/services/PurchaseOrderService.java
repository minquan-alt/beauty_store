package com.beautystore.adeline.services;

import java.math.BigDecimal;
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
import com.beautystore.adeline.repository.PurchaseOrderProcedureRepository;
import com.beautystore.adeline.repository.PurchaseOrderRepository;

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
    private PurchaseOrderProcedureRepository purchaseOrderProcedureRepository;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private  PurchaseOrderResponseMapper purchaseOrderResponseMapper;



    @Autowired
    private ProductRepository productRepository;

public PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request) {
    try {
        /*---------------------------------------------------------
         * B1: Sử dụng Repository với giải pháp không dùng UDT
         *--------------------------------------------------------*/
        List<Long> productIds = request.getItems().stream()
                .map(PurchaseOrderRequest.PurchaseItem::getProductId)
                .toList();
        
        List<Integer> quantities = request.getItems().stream()
                .map(PurchaseOrderRequest.PurchaseItem::getQuantity)
                .toList();
        
        List<BigDecimal> unitPrices = request.getItems().stream()
                .map(PurchaseOrderRequest.PurchaseItem::getUnitPrice)
                .toList();

        // Gọi repository method mới (không dùng UDT)
        Long createdPurchaseOrderId = purchaseOrderProcedureRepository
                .processPurchaseOrder(productIds, quantities, unitPrices);

        /*---------------------------------------------------------
         * B2: Lấy đơn hàng vừa tạo bằng ID trả về
         *--------------------------------------------------------*/
        PurchaseOrder order = purchaseOrderRepository
                .findById(createdPurchaseOrderId)
                .orElseThrow(() -> 
                        new RuntimeException("Không tìm thấy Purchase Order với ID: " + createdPurchaseOrderId));

        /*---------------------------------------------------------
         * B3: Trả về DTO
         *--------------------------------------------------------*/
        return PurchaseOrderResponse.builder()
                .purchaseOrderId(order.getId())
                .orderDate(order.getOrderDate())
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
        throw new RuntimeException("Lỗi khi tạo purchase order: " + e.getMessage(), e);
    }
}

    public int countPurchaseOrders(){
        return (int) purchaseOrderRepository.count();
    }

    public Page<PurchaseOrderResponse> getPurchaseOrdersPage(int page, int size){
        if (page < 0) {
                page = 0;
            }
            if (size < 1) {
                size = 10; // Set default size
            }
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
