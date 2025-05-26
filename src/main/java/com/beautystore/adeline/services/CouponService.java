package com.beautystore.adeline.services;


import java.util.List;

import com.beautystore.adeline.dto.request.CouponCreateRequest;
import com.beautystore.adeline.dto.request.CouponUpdateRequest;
import com.beautystore.adeline.dto.response.CouponResponse;
import com.beautystore.adeline.entity.Coupon;
import com.beautystore.adeline.exception.AppException;
import com.beautystore.adeline.exception.ErrorCode;
import com.beautystore.adeline.mapper.CouponMapper;
import com.beautystore.adeline.repository.CouponRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CouponService {

    private static Logger logger = LoggerFactory.getLogger(CouponService.class);

    @Autowired
    private final CouponRepository couponRepository;

    @Autowired
    private final CouponMapper couponMapper;
public CouponResponse createCoupon(CouponCreateRequest request) {
    if(this.couponRepository.existsById(request.getCode()))
        throw new AppException(ErrorCode.COUPON_EXISTED);
    
    Coupon coupon = this.couponMapper.toCoupon(request);
    Coupon savedCoupon = this.couponRepository.save(coupon);
    return this.couponMapper.toResponse(savedCoupon);
}

public List<Coupon> getCoupons() {
    return this.couponRepository.findAll();
}

public CouponResponse getCouponById(String code) {
    logger.info("Fetching coupon with code: {}", code);

    Coupon coupon = this.couponRepository.findById(code)
        .orElseThrow(() -> {
            logger.error("Coupon not found with code: {}", code);
            return new AppException(ErrorCode.COUPON_NOT_FOUND);
        });
    logger.info("Coupon found: {}", coupon);

    return this.couponMapper.toResponse(coupon);
}


public CouponResponse updateCouponById(CouponUpdateRequest request, String code) {
    Coupon coupon = this.couponRepository.findById(code)
        .orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_FOUND));
    
    this.couponMapper.updateEntity(coupon, request);
    Coupon updatedCoupon = this.couponRepository.save(coupon);
    
    return this.couponMapper.toResponse(updatedCoupon);
}

// public boolean useCouponById(long id) {
//     Coupon coupon = this.couponRepository.findById(id)
//         .orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_FOUND));
    
    
// }

    public void deleteCouponById(String code){
        this.couponRepository.deleteById(code);
    }

}