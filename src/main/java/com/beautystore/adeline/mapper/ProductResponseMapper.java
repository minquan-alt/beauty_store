package com.beautystore.adeline.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.beautystore.adeline.dto.request.ProductUpdateRequest;
import com.beautystore.adeline.dto.response.ProductResponse;
import com.beautystore.adeline.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductResponseMapper {

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "supplierName", source = "supplier.name")
    ProductResponse toResponse(Product product);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateProduct(@MappingTarget Product product, ProductUpdateRequest request);

    default List<ProductResponse> toResponseList(List<Product> products) {
        return products.stream()
                .map(this::toResponse)
                .toList();
    }
}