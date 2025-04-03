package com.beautystore.adeline.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {
  Long cartId;
  Long userId;
  List<CartItemResponse> items;
  Double totalCartPrice;
  Integer totalItems;
}