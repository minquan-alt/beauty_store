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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.beautystore.adeline.dto.request.ProductCreateRequest;
import com.beautystore.adeline.dto.request.ProductUpdateRequest;
import com.beautystore.adeline.dto.response.ApiResponse;
import com.beautystore.adeline.dto.response.GetProductImageResponse;
import com.beautystore.adeline.dto.response.ProductResponse;
import com.beautystore.adeline.services.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping("")
    ApiResponse<ProductResponse> createProduct(@RequestBody ProductCreateRequest request) {
        ApiResponse<ProductResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(productService.createProduct(request));
        return apiResponse;
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> getProducts() {
        ApiResponse<List<ProductResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(productService.getAllProducts());

        return apiResponse;
    }

    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse> getProduct(@PathVariable Long productId) {
        ApiResponse<ProductResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(productService.getProductById(productId));

        return apiResponse;
    }

    @PutMapping("/{productId}")
    public ApiResponse<ProductResponse> updateProduct(
            @RequestBody ProductUpdateRequest request,
            @PathVariable Long productId) {
        ApiResponse<ProductResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(productService.updateProduct(request, productId));

        return apiResponse;
    }

    // Delete Product
    @DeleteMapping("/{productId}")
    public ApiResponse<String> deleteProduct(@PathVariable Long productId) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        productService.deleteProduct(productId);
        apiResponse.setResult("Product deleted successfully");
        return apiResponse;
    }

    @GetMapping("/searchByQuery")
    public ApiResponse<List<ProductResponse>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) List<String> category) {
        ApiResponse<List<ProductResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(productService.searchProducts(keyword, minPrice, maxPrice, category));
        return apiResponse;
    }

    @GetMapping("/{productId}/images")
    public ApiResponse<GetProductImageResponse> getProductImage(@PathVariable Long productId) {
        ApiResponse<GetProductImageResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(productService.getProductImages(productId));
        return apiResponse;
    }

}
