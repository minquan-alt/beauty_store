package com.beautystore.adeline.mapper;

import com.beautystore.adeline.entity.ProductImage;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {
    default String map(ProductImage image) {
        return image != null ? image.getImageUrl() : null;
    }
}