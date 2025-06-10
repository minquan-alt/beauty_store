package com.beautystore.adeline.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beautystore.adeline.dto.request.SupplierCreateRequest;
import com.beautystore.adeline.dto.response.ApiResponse;
import com.beautystore.adeline.dto.response.SupplierResponse;
import com.beautystore.adeline.entity.Supplier;
import com.beautystore.adeline.services.SupplierService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {
    
    @Autowired
    private SupplierService supplierService;

    @PostMapping()
    ApiResponse<Supplier> createSupplier(@RequestBody @Valid SupplierCreateRequest request){
         System.out.println("Received request" + request);
        ApiResponse<Supplier> apiResponse = new ApiResponse<>();
        apiResponse.setResult(supplierService.createSupplier(request));
        return apiResponse;
    }

    @GetMapping()
    ApiResponse<List<Supplier>> getSuppliers(){
        ApiResponse<List<Supplier>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(supplierService.getSuppliers());
        return apiResponse;
    }

    @GetMapping("/{supplierId}")
    ApiResponse<SupplierResponse> getSupplier(@PathVariable Long supplierId){
        ApiResponse<SupplierResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(supplierService.getSupplier(supplierId));
        return apiResponse;
    }

    @PutMapping("/{supplierId}")
    ApiResponse<SupplierResponse> updateSupplier(@RequestBody SupplierCreateRequest request, @PathVariable("supplierId" ) Long supplierId){
        ApiResponse<SupplierResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(supplierService.updateSupplier(request, supplierId));
        return apiResponse;
    }

    @DeleteMapping("/{supplierId}")
    ApiResponse<String> deleteSupplier (@PathVariable Long supplierId){
        ApiResponse<String> apiResponse= new ApiResponse<>();
        supplierService.deleteSupplier(supplierId);
        apiResponse.setResult("Supplier has been deleted");
        return apiResponse;
    }
}
