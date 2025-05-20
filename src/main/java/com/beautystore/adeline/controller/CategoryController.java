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

import com.beautystore.adeline.dto.request.CategoryCreateRequest;
import com.beautystore.adeline.dto.response.ApiResponse;
import com.beautystore.adeline.dto.response.CategoryResponse;
import com.beautystore.adeline.entity.Category;
import com.beautystore.adeline.services.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    

    @Autowired
    private CategoryService categoryService;

    //create category
    @PostMapping()
    ApiResponse<Category> createCategory(@RequestBody @Valid CategoryCreateRequest request){
        System.out.println("Received request" + request);
        ApiResponse<Category> apiResponse = new ApiResponse<>();
        apiResponse.setResult(categoryService.createCategory(request));
        return apiResponse;
    }

    //read categories
    @GetMapping()
    ApiResponse<List<Category>> getCategories(){
        ApiResponse<List<Category>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(categoryService.getCategories());
        return apiResponse;
    }

    //read category
    @GetMapping("/{categoryId}")
    ApiResponse<CategoryResponse> getCategory(@PathVariable Long categoryId) {
        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(categoryService.getCategory(categoryId));
        return apiResponse;
    }

    //update category
    @PutMapping("/{categoryId}")
    ApiResponse<CategoryResponse> updateCategory(@RequestBody CategoryCreateRequest request, @PathVariable("categoryId" ) Long categoryId){
        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(categoryService.updateCategory(request, categoryId));
        return apiResponse;
    }

    //delete category
    @DeleteMapping("/{categoryId}")
    ApiResponse<String> deleteCategory(@PathVariable Long categoryId){
        ApiResponse<String> apiResponse = new ApiResponse<>();
        categoryService.deleteCategory(categoryId);
        System.out.println(categoryId);
        apiResponse.setResult("Category has been deleted");
        return apiResponse;
    }
}
