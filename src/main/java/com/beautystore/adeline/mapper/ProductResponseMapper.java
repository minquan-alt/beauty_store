package com.beautystore.adeline.mapper;

import org.mapstruct.*;
import com.beautystore.adeline.dto.request.ProductUpdateRequest;
import com.beautystore.adeline.dto.response.ProductResponse;
import com.beautystore.adeline.entity.Product;
import java.util.List;

@Mapper(componentModel = "spring",
        uses = {ProductImageMapper.class, ReviewMapper.class},
        imports = {java.util.stream.Collectors.class, java.util.Collections.class, 
                  com.beautystore.adeline.entity.ProductImage.class}, // Thêm import ProductImage
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductResponseMapper {

    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "supplierName", source = "supplier.name")
    @Mapping(target = "imageUrls", 
             expression = "java(product.getImages() != null ? "
                        + "product.getImages().stream().map(img -> img.getImageUrl()).collect(java.util.stream.Collectors.toList()) "
                        + ": java.util.Collections.emptyList())")
    @Mapping(target = "averageRating", expression = "java(calculateAverageRating(product))")
    ProductResponse toResponse(Product product);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "inventory", ignore = true)
    @Mapping(target = "cartItems", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateProduct(@MappingTarget Product product, ProductUpdateRequest request);

    default List<ProductResponse> toResponseList(List<Product> products) {
        return products.stream()
                .map(this::toResponse)
                .toList();
    }

    default Double calculateAverageRating(Product product) {
        if (product.getReviews() == null || product.getReviews().isEmpty()) {
            return 0.0;
        }
        return product.getReviews().stream()
                .mapToDouble(review -> review.getRating() != null ? review.getRating() : 0)
                .average()
                .orElse(0.0);
    }
}