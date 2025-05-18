package com.beautystore.adeline.mapper;

import java.time.LocalDateTime;

import com.beautystore.adeline.dto.request.CouponCreateRequest;
import com.beautystore.adeline.dto.request.CouponUpdateRequest;
import com.beautystore.adeline.dto.response.CouponResponse;
import com.beautystore.adeline.entity.Coupon;
import com.beautystore.adeline.entity.User;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports = {LocalDateTime.class})
public interface CouponMapper {

    // Changed method name from toEntity to toCoupon
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usedCount", constant = "0")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "applicableUser", source = "applicableUserId", qualifiedByName = "mapUserIdToUser")
    @Mapping(target = "value", source = "discountValue")
    Coupon toCoupon(CouponCreateRequest request);

    @Mapping(target = "discountValue", source = "value") // Maps value to discountValue
    @Mapping(target = "applicableUserId", source = "applicableUser.id")
    CouponResponse toResponse(Coupon coupon);

    @Mapping(target = "applicableUser", source = "applicableUserId", qualifiedByName = "mapUserIdToUser")
    @Mapping(target = "value", source = "discountValue")
    void updateEntity(@MappingTarget Coupon coupon, CouponUpdateRequest request);

    @Named("mapUserIdToUser")
    default User mapUserIdToUser(Long userId) {
        if (userId == null) return null;
        return User.builder().id(userId).build();
    }

    @AfterMapping
    default void setDefaultValues(@MappingTarget Coupon coupon) {
        if (coupon.getUsedCount() == null) coupon.setUsedCount(0);
        if (coupon.getActive() == null) coupon.setActive(true);
        if (coupon.getCreatedAt() == null) coupon.setCreatedAt(LocalDateTime.now());
    }
}