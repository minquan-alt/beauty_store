package com.beautystore.adeline.dto.request;

import com.beautystore.adeline.entity.Coupon;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponCreateRequest {
    @NotBlank
    private String code;
    
    @NotNull
    private Coupon.CouponType type;
    
    @NotNull
    @DecimalMin("0.00")
    private BigDecimal discountValue;
    
    @DecimalMin("0.00")
    private BigDecimal minOrderAmount;
    
    @DecimalMin("0.00")
    private BigDecimal maxDiscountAmount;
    
    @NotNull
    private LocalDate startDate;
    
    @NotNull
    private LocalDate expirationDate;
    
    @PositiveOrZero
    private Integer usageLimit;
    
    private Long applicableUserId;
}