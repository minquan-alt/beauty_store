package com.beautystore.adeline.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {
    private Long orderId;
    private String customerName;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String address;
    private List<OrderItemResponse> items;
    
    @Data
    @Builder
    public static class OrderItemResponse {
        private String productName;
        private Integer quantity;
        private BigDecimal price;
    }
}
