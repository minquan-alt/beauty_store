package com.beautystore.adeline.mapper;

import org.mapstruct.Mapper;

import com.beautystore.adeline.dto.request.CouponCreateRequest;
import com.beautystore.adeline.dto.response.CouponResponse;
import com.beautystore.adeline.entity.Coupon;

@Mapper(componentModel = "spring")
public interface CouponMapper {
    Coupon toCoupon(CouponCreateRequest request);
    CouponResponse toCouponMapper(Coupon coupon);
}
