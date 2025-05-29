package com.beautystore.adeline.controller;

import com.beautystore.adeline.dto.request.AddToCartRequest;
import com.beautystore.adeline.dto.request.UpdateCartItemRequest;
import com.beautystore.adeline.dto.response.AddCartResponse;
import com.beautystore.adeline.dto.response.ApiResponse;
import com.beautystore.adeline.dto.response.CartResponse;
import com.beautystore.adeline.services.CartService;
import com.beautystore.adeline.services.UserService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cart")
public class CartController {

  private final CartService cartService;
  private final UserService userService;

  public CartController(CartService cartService, UserService userService) {
    this.cartService = cartService;
    this.userService = userService;
  }

  @GetMapping()
  public ApiResponse<CartResponse> getCart(HttpSession session) {
    Long userId = userService.getUserIdFromSession(session);
    ApiResponse<CartResponse> response = new ApiResponse<>();
    response.setResult(cartService.getCart(userId));
    return response;
  }


  @PostMapping("/add")
  public ApiResponse<AddCartResponse> addToCart(@RequestBody AddToCartRequest request, HttpSession session) {
      Long userId = userService.getUserIdFromSession(session);
      AddCartResponse result = cartService.addToCart(request, userId);
      ApiResponse<AddCartResponse> apiResponse = new ApiResponse<>();
      apiResponse.setResult(result);

      return apiResponse;
  }


  @PutMapping("/update/{productId}")
  public ApiResponse<CartResponse> updateCartItem(
      @PathVariable Long productId,
      @Valid @RequestBody UpdateCartItemRequest request,
      HttpSession session) {
    Long userId = userService.getUserIdFromSession(session);
    ApiResponse<CartResponse> response = new ApiResponse<>();
    response.setResult(cartService.updateCartItem(userId, productId, request));
    return response;
  }

  @DeleteMapping("/clear/{productId}")
  public ApiResponse<String> removeFromCart(
      @PathVariable Long productId,
      HttpSession session) {
    Long userId = userService.getUserIdFromSession(session);
    ApiResponse<String> response = new ApiResponse<>();
    cartService.removeFromCart(userId, productId);
    response.setResult("Product with id:  " + productId + " has been removed from cart");
    return response;
  }

  @DeleteMapping("/clear")
  public ApiResponse<String> clearCart(HttpSession session) {
    Long userId = userService.getUserIdFromSession(session);
    cartService.clearCart(userId);
    ApiResponse<String> response = new ApiResponse<>();
    response.setResult("Cart cleared successfully");
    return response;
  }

}