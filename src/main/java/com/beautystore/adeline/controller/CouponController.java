package com.beautystore.adeline.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beautystore.adeline.dto.request.ApiResponse;
import com.beautystore.adeline.dto.request.CouponCreateRequest;
import com.beautystore.adeline.dto.response.CouponResponse;
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
        apiResponse.setResult(this.couponService.createCoupon(request));
        return apiResponse;
    }    

    @GetMapping()
    ApiResponse<List<Coupon>> getCoupons(){
        ApiResponse<List<Coupon>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(this.couponService.getCoupons());
        return apiResponse;
    }

    @GetMapping("{couponId}")
    ApiResponse<CouponResponse> getCoupon(@PathVariable Long couponId) {
        ApiResponse<CouponResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(this.couponService.getCouponByID(couponId));
        return apiResponse;
    }

    @GetMapping("/code/{code}")
    ApiResponse<CouponResponse> getCoupon(@PathVariable String code) {
        ApiResponse<CouponResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(this.couponService.getCouponByCode(code));
        return apiResponse;
    }
}
