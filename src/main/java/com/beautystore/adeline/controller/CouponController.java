package com.beautystore.adeline.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beautystore.adeline.dto.request.ApiResponse;
import com.beautystore.adeline.dto.request.CouponCreateRequest;
import com.beautystore.adeline.entity.Coupon;
import com.beautystore.adeline.services.CouponService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/coupons")
public class CouponController {
    

    @Autowired
    private CouponService couponService;

    @PostMapping()
    ApiResponse<Coupon> createCoupon(@RequestBody @Valid CouponCreateRequest request){
        ApiResponse<Coupon> apiResponse = new ApiResponse<>();
        apiResponse.setResult(couponService.createCoupon(request));
        return apiResponse;
    }    
}
