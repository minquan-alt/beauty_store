package com.beautystore.adeline.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.beautystore.adeline.dto.request.PurchaseOrderRequest;
import com.beautystore.adeline.dto.response.ApiResponse;
import com.beautystore.adeline.dto.response.PagedResponse;
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

    @GetMapping
    public ApiResponse<PagedResponse<PurchaseOrderResponse>> getPurchaseOrders(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        int actualPage = (page == null || page < 1) ? 1 : page;
        int actualSize = (size == null || size < 1) ? purchaseOrderService.countPurchaseOrders() : size;
        
        Page<PurchaseOrderResponse> purchaseOrderPage = purchaseOrderService.getPurchaseOrdersPage(actualPage - 1, actualSize);

        PagedResponse<PurchaseOrderResponse> pagedResponse = new PagedResponse<>(
            purchaseOrderPage.getContent(),
            new PagedResponse.Meta(
                actualPage, 
                actualSize, 
                purchaseOrderPage.getTotalPages(), 
                purchaseOrderPage.getTotalElements()
                )
        );
        ApiResponse<PagedResponse<PurchaseOrderResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(pagedResponse);
        return apiResponse;
    }

    @GetMapping("/{purchaseOrderId}")
    public ApiResponse<PurchaseOrderResponse> getPurchaseOrder(@PathVariable Long purchaseOrderId){
        ApiResponse<PurchaseOrderResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(purchaseOrderService.getPurchaseOrderById(purchaseOrderId));
        return apiResponse;
    }
    
    @PutMapping("/{id}/approve")
    public ApiResponse<PurchaseOrderResponse> approvePurchaseOrder(@PathVariable Long id) {
        ApiResponse<PurchaseOrderResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(purchaseOrderService.updateOrderStatus(id, "Completed"));
        return apiResponse;
    }

    @PutMapping("/{id}/decline")
    public ApiResponse<PurchaseOrderResponse> declinePurchaseOrder(@PathVariable Long id) {
        ApiResponse<PurchaseOrderResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(purchaseOrderService.updateOrderStatus(id, "Cancelled"));
        return apiResponse;
    }

}