package com.beautystore.adeline.controller;

import com.beautystore.adeline.dto.request.InventoryCreateRequest;
import com.beautystore.adeline.dto.response.ApiResponse;
import com.beautystore.adeline.dto.response.InventoryResponse;
import com.beautystore.adeline.entity.Inventory;
import com.beautystore.adeline.services.InventoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/inventories")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @PostMapping
    public ApiResponse<Inventory> createInventory(@RequestBody @Valid InventoryCreateRequest request) {
        ApiResponse<Inventory> apiResponse = new ApiResponse<>();
        apiResponse.setResult(inventoryService.createInventory(request));
        return apiResponse;
    }

    @GetMapping
    public ApiResponse<List<Inventory>> getInventories() {
        ApiResponse<List<Inventory>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(inventoryService.getInventories());
        return apiResponse;
    }

    @GetMapping("/{inventoryId}")
    public ApiResponse<InventoryResponse> getInventory(@PathVariable Long inventoryId) {
        ApiResponse<InventoryResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(inventoryService.getInventory(inventoryId));
        return apiResponse;
    }

    @PutMapping("/{inventoryId}")
    public ApiResponse<InventoryResponse> updateInventory(@RequestBody @Valid InventoryCreateRequest request,
                                                          @PathVariable Long inventoryId) {
        ApiResponse<InventoryResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(inventoryService.updateInventory(request, inventoryId));
        return apiResponse;
    }

    @DeleteMapping("/{inventoryId}")
    public ApiResponse<String> deleteInventory(@PathVariable Long inventoryId) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        inventoryService.deleteInventory(inventoryId);
        apiResponse.setResult("Inventory has been deleted");
        return apiResponse;
    }
}
