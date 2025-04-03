package com.beautystore.adeline.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {
  Long productId;
  String productName;
  String productImage;
  Double unitPrice;
  Integer quantity;
  Double totalPrice;
}