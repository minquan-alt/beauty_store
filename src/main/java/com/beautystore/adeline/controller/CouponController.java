package com.beautystore.adeline.controller;

import java.util.List;

import com.beautystore.adeline.dto.request.CouponCreateRequest;
import com.beautystore.adeline.dto.request.CouponUpdateRequest;
import com.beautystore.adeline.dto.response.ApiResponse;
import com.beautystore.adeline.dto.response.CouponResponse;
import com.beautystore.adeline.entity.Coupon;
import com.beautystore.adeline.services.CouponService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/coupons")
public class CouponController {
    

    @Autowired
    private CouponService couponService;

    @PostMapping()
    ApiResponse<CouponResponse> createCoupon(@RequestBody @Valid CouponCreateRequest request){
        ApiResponse<CouponResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(this.couponService.createCoupon(request));
        return apiResponse;
    }    

    @GetMapping()
    ApiResponse<List<Coupon>> getCoupons(){
        ApiResponse<List<Coupon>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(this.couponService.getCoupons());
        return apiResponse;
    }

    @GetMapping("/{couponCode}")
    ApiResponse<CouponResponse> getCoupon(@PathVariable String couponCode) {
        ApiResponse<CouponResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(this.couponService.getCouponById(couponCode));
        return apiResponse;
    }

    @PutMapping("/{code}")
    ApiResponse<CouponResponse> updateCouponById(@RequestBody CouponUpdateRequest request, @PathVariable("code") String code){
        ApiResponse<CouponResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(this.couponService.updateCouponById(request, code));
        return apiResponse;
    }

    @DeleteMapping("/{code}")
    ApiResponse<String> deleteCouponById(@PathVariable String code){
        ApiResponse<String> apiResponse = new ApiResponse<>();
        this.couponService.deleteCouponById(code);
        apiResponse.setResult("Coupon has been deleted");
        return apiResponse;
    }

    // @PostMapping("use/{couponId}")
    // public ApiResponse<Boolean> useCouponById(@PathVariable Long couponId) {
    //     //TODO: process POST request
    //     ApiResponse<Boolean> apiResponse = new ApiResponse<>();
    //     apiResponse.setResult(couponService.useCouponById(couponId)); 
        
    //     return true;
    // }
    

}
