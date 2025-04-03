package com.beautystore.adeline.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddToCartRequest {
  @NotNull(message = "PRODUCT_ID_REQUIRED")
  Long productId;

  @NotNull(message = "QUANTITY_REQUIRED")
  @Min(value = 1, message = "QUANTITY_INVALID")
  Integer quantity;
}