package com.beautystore.adeline.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.beautystore.adeline.dto.response.CartItemResponse;
import com.beautystore.adeline.dto.response.CartResponse;
import com.beautystore.adeline.entity.Cart;
import com.beautystore.adeline.entity.CartItem;

@Mapper(componentModel = "spring")
public interface CartMapper {
  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "items", source = "cartItems")
  CartResponse toCartResponse(Cart cart);

  @Mapping(target = "productId", source = "product.id")
  @Mapping(target = "productName", source = "product.name")
  @Mapping(target = "productImage", expression = "java(cartItem.getProduct().getImages().isEmpty() ? null : cartItem.getProduct().getImages().get(0).getImageUrl())")
  @Mapping(target = "unitPrice", source = "product.price")
  @Mapping(target = "totalPrice", expression = "java(cartItem.getQuantity() * cartItem.getProduct().getPrice())")
  CartItemResponse toCartItemResponse(CartItem cartItem);
}