package com.beautystore.adeline.dto.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String categoryName;
    private String supplierName;
    private List<String> imageUrls;
    private Double averageRating;
    // Các trường khác nếu cần
}