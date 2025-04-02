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
public class UpdateCartItemRequest {
  @NotNull(message = "QUANTITY_REQUIRED")
  @Min(value = 0, message = "QUANTITY_INVALID")
  Integer quantity;
}