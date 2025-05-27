package com.beautystore.adeline.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beautystore.adeline.dto.request.PurchaseOrderRequest;
import com.beautystore.adeline.dto.response.ApiResponse;
import com.beautystore.adeline.dto.response.PurchaseOrderResponse;
import com.beautystore.adeline.services.PurchaseOrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/purchase-orders")
public class PurchaseOrderController {
    
    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ApiResponse<PurchaseOrderResponse> createPurchaseOrder(@RequestBody @Valid PurchaseOrderRequest request){
        System.out.println("Received request" + request);
        ApiResponse<PurchaseOrderResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(purchaseOrderService.createPurchaseOrder(request));
        return apiResponse;
    }
}
