package com.beautystore.adeline.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PurchaseOrderResponse {
    private Long purchaseOrderId;
    private String supplierName;
    private LocalDateTime orderDate;
    private String status;
    private List<PurchaseOrderDetailResponse> items;
}
