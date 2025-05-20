package com.beautystore.adeline.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.beautystore.adeline.dto.request.CategoryCreateRequest;
import com.beautystore.adeline.dto.response.CategoryResponse;
import com.beautystore.adeline.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryCreateRequest Request);
    CategoryResponse toCategoryResponse(Category category);
    void updateCategory(@MappingTarget Category category, CategoryCreateRequest request);
}
