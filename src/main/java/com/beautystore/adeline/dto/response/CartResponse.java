package com.beautystore.adeline.dto.response;

import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

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