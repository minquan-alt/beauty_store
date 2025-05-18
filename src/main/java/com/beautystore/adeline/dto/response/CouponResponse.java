package com.beautystore.adeline.dto.response;

import com.beautystore.adeline.entity.Coupon;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponse {
    private Long id;
    private String code;
    private Coupon.CouponType type;
    private BigDecimal discountValue;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscountAmount;
    private LocalDate startDate;
    private LocalDate expirationDate;
    private Integer usageLimit;
    private Integer usedCount;
    private Boolean active;
    private Long applicableUserId;
    private LocalDateTime createdAt;
}