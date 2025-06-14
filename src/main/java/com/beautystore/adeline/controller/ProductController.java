package com.beautystore.adeline.controller;

import java.math.BigDecimal;
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
import com.beautystore.adeline.dto.response.PagedResponse;
import com.beautystore.adeline.dto.response.ProductResponse;
import com.beautystore.adeline.services.ProductService;
import org.springframework.data.domain.Page;

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
    public ApiResponse<PagedResponse<ProductResponse>> getProducts(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        int actualPage = (page == null || page < 1) ? 1 : page;
        int actualSize = (size == null || size < 1) ? productService.countProducts() : size;

        Page<ProductResponse> productPage = productService.getProductsPage(actualPage - 1, actualSize);

        PagedResponse<ProductResponse> pagedResponse = new PagedResponse<>(
                productPage.getContent(),
                new PagedResponse.Meta(
                        actualPage,
                        actualSize,
                        productPage.getTotalPages(),
                        productPage.getTotalElements()));
        ApiResponse<PagedResponse<ProductResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(pagedResponse);
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
    public ApiResponse<PagedResponse<ProductResponse>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) List<String> category,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        int actualPage = (page == null || page < 1) ? 1 : page;
        int actualSize = (size == null || size < 1) ? productService.countProducts() : size;

        Page<ProductResponse> searchProductPage = productService.searchProducts(keyword, minPrice, maxPrice, category,
                actualPage - 1, actualSize);

        PagedResponse<ProductResponse> pagedResponse = new PagedResponse<>(
                searchProductPage.getContent(),
                new PagedResponse.Meta(
                        actualPage,
                        actualSize,
                        searchProductPage.getTotalPages(),
                        searchProductPage.getTotalElements()));
        ApiResponse<PagedResponse<ProductResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(pagedResponse);
        return apiResponse;
    }

    @GetMapping("/{productId}/images")
    public ApiResponse<GetProductImageResponse> getProductImage(@PathVariable Long productId) {
        ApiResponse<GetProductImageResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(productService.getProductImages(productId));
        return apiResponse;
    }

    @GetMapping("/search")
    public ApiResponse<PagedResponse<ProductResponse>> adminSearch(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(name = "category_id", required = false) Long categoryId,
            @RequestParam(required = false) String sort) {

        int actualPage = (page == null || page < 1) ? 1 : page;
        int actualSize = (size == null || size < 1) ? productService.countProducts() : size;

        Page<ProductResponse> productPage = productService.getProductsPageWithFilterSort(
            actualPage - 1, actualSize, name, categoryId, sort);

        PagedResponse<ProductResponse> pagedResponse = new PagedResponse<>(
                productPage.getContent(),
                new PagedResponse.Meta(
                        actualPage,
                        actualSize,
                        productPage.getTotalPages(),
                        productPage.getTotalElements()));
        ApiResponse<PagedResponse<ProductResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(pagedResponse);
        return apiResponse;
    }

}
