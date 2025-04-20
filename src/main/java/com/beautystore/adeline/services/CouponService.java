package com.beautystore.adeline.services;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beautystore.adeline.dto.request.CouponCreateRequest;
import com.beautystore.adeline.dto.request.CouponUpdateRequest;
import com.beautystore.adeline.dto.response.CouponResponse;
import com.beautystore.adeline.entity.Coupon;
import com.beautystore.adeline.exception.AppException;
import com.beautystore.adeline.exception.ErrorCode;
import com.beautystore.adeline.mapper.CouponMapper;
import com.beautystore.adeline.repository.CouponRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CouponService {

    private static Logger logger = LoggerFactory.getLogger(CouponService.class);

    @Autowired
    private final CouponRepository couponRepository;

    @Autowired
    private final CouponMapper couponMapper;

    public Coupon createCoupon(CouponCreateRequest request) {
        if(this.couponRepository.existsByCode(request.getCode()))
            throw new AppException(ErrorCode.COUPON_EXISTED);
        Coupon coupon = this.couponMapper.toCoupon(request);
        return this.couponRepository.save(coupon);
    }

    public List<Coupon> getCoupons(){
        return this.couponRepository.findAll();
    }

    public CouponResponse getCouponByID(Long id){
        logger.info("Fetching coupon with id: {}", id);

        Coupon coupon = this.couponRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Coupon not found with id: {}", id);
                return new AppException(ErrorCode.COUPON_NOT_FOUND);
            });
        logger.info("Coupon found: {}" , coupon);

        CouponResponse response = this.couponMapper.toCouponResponse(coupon);
        logger.info("Mapped coupon to response: {}", response);
        
        return response;
    }

    public CouponResponse getCouponByCode(String code){
        logger.info("Fetching coupon with code: {}", code);

        Coupon coupon = this.couponRepository.findByCode(code)
            .orElseThrow(() -> {
                logger.error("Coupon not found with code: {}", code);
                return new AppException(ErrorCode.COUPON_NOT_FOUND);
            });
        logger.info("Coupon found: {}" , coupon);

        CouponResponse response = this.couponMapper.toCouponResponse(coupon);
        logger.info("Mapped coupon to response: {}", response);
        
        return response;
    }

    public CouponResponse updateCouponByID(CouponUpdateRequest request, Long id){
        Coupon coupon = this.couponRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Coupon not found"));
        this.couponMapper.updateCoupon(coupon, request);

        return this.couponMapper.toCouponResponse(this.couponRepository.save(coupon));
    }


}