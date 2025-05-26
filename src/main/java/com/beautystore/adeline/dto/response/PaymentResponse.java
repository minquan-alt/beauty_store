package com.beautystore.adeline.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.beautystore.adeline.entity.Order.PaymentMethod;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
    private Long id;
    private String transactionId;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private Long orderId;
    private Long userId;
    private LocalDateTime createdAt;
}
