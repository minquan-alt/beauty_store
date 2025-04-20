package com.beautystore.adeline.services;


// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beautystore.adeline.dto.request.CouponCreateRequest;
import com.beautystore.adeline.entity.Coupon;
import com.beautystore.adeline.exception.AppException;
import com.beautystore.adeline.exception.ErrorCode;
import com.beautystore.adeline.mapper.CouponMapper;
import com.beautystore.adeline.repository.CouponRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CouponService {

    // private static Logger logger = LoggerFactory.getLogger(CouponService.class);

    @Autowired
    private final CouponRepository couponRepository;

    @Autowired
    private final CouponMapper couponMapper;

    public Coupon createCoupon(CouponCreateRequest request) {
        if(couponRepository.existsByCode(request.getCode()))
            throw new AppException(ErrorCode.COUPON_EXISTED);
        Coupon coupon = couponMapper.toCoupon(request);
        return couponRepository.save(coupon);
    }

}