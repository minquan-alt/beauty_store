package com.beautystore.adeline.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;


@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CouponCreateRequest {
    String code;
    BigDecimal discount;
    BigDecimal maxDiscountAmount;
    LocalDate expirationDate;
}
