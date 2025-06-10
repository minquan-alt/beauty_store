package com.beautystore.adeline.mapper;

import com.beautystore.adeline.dto.response.PurchaseOrderDetailResponse;
import com.beautystore.adeline.dto.response.PurchaseOrderResponse;
import com.beautystore.adeline.entity.PurchaseOrder;
import com.beautystore.adeline.entity.PurchaseOrderDetail;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PurchaseOrderResponseMapper {
    
    @Mapping(target = "purchaseOrderId", source = "id")
    @Mapping(target = "items", expression = "java(mapOrderDetails(purchaseOrder.getOrderDetails()))")
    @Mapping(target = "status", expression = "java(purchaseOrder.getStatus().name())")
    PurchaseOrderResponse toResponse(PurchaseOrder purchaseOrder);

    default List<PurchaseOrderDetailResponse> mapOrderDetails(List<PurchaseOrderDetail> details) {
        return details.stream().map(detail -> 
            PurchaseOrderDetailResponse.builder()
                .productId(detail.getProduct().getId())
                .productName(detail.getProduct().getName())
                .quantity(detail.getQuantity())
                .unitPrice(detail.getUnitPrice())
                .build()
        ).collect(Collectors.toList());
    }

    default List<PurchaseOrderResponse> toResponseList(List<PurchaseOrder> purchaseOrders) {
        return purchaseOrders.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
