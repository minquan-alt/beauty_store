package com.beautystore.adeline.controller;

import java.util.List;

import com.beautystore.adeline.dto.request.AddAddressRequest;
import com.beautystore.adeline.dto.request.UpdateAddressRequest;
import com.beautystore.adeline.dto.response.AddressResponse;
import com.beautystore.adeline.dto.response.ApiResponse;
import com.beautystore.adeline.services.AddressService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/address")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @GetMapping()
    public ApiResponse<List<AddressResponse>> getAddresses(HttpSession session) {
        ApiResponse<List<AddressResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(addressService.getAddresses(session));
        return apiResponse;
    }

    @GetMapping("/{addressId}")
    public ApiResponse<AddressResponse> getAddress(@PathVariable Long addressId) {
        ApiResponse<AddressResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(addressService.getAddress(addressId));
        return apiResponse;
    }

    @PostMapping("/add")
    public ApiResponse<AddressResponse> addAddress(@RequestBody AddAddressRequest request, HttpSession session) {
        ApiResponse<AddressResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(addressService.addAddress(session, request));
        return apiResponse;
    }

    @PutMapping("/update")
    public ApiResponse<AddressResponse> updateAddress(@RequestBody UpdateAddressRequest request) {
        ApiResponse<AddressResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(addressService.updateAddress(request));
        return apiResponse;
    }

    @PutMapping("/update-default/{addressId}")
    public ApiResponse<Boolean> updateDefaultAddress(HttpSession session, Long addressId) {
        ApiResponse<Boolean> apiResponse = new ApiResponse<>();
        apiResponse.setResult(addressService.updateDefaultAddress(session, addressId));
        return apiResponse;
    }


    
}
