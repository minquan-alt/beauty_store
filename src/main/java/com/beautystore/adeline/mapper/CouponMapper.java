package com.beautystore.adeline.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.beautystore.adeline.dto.request.CouponCreateRequest;
import com.beautystore.adeline.dto.request.CouponUpdateRequest;
import com.beautystore.adeline.dto.response.CouponResponse;
import com.beautystore.adeline.entity.Coupon;

@Mapper(componentModel = "spring")
public interface CouponMapper {
    Coupon toCoupon(CouponCreateRequest request);
    CouponResponse toCouponResponse(Coupon coupon);
    void updateCoupon(@MappingTarget Coupon coupon, CouponUpdateRequest request);
}
