package com.beautystore.adeline.dto.request;

import java.math.BigDecimal;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ProductUpdateRequest {
    String name;
    String description;
    BigDecimal price;
    Long supplier_id;
    Long category_id;
    Long inventory_id;
    List<String> imageUrls;
}
