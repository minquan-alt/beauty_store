package com.beautystore.adeline.controller;

import com.beautystore.adeline.dto.request.AddToCartRequest;
import com.beautystore.adeline.dto.request.ApiResponse;
import com.beautystore.adeline.dto.request.UpdateCartItemRequest;
import com.beautystore.adeline.dto.response.CartResponse;
import com.beautystore.adeline.services.CartService;
import com.beautystore.adeline.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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
  public ApiResponse<CartResponse> addToCart(
      @Valid @RequestBody AddToCartRequest request,
      HttpSession session) {
    Long userId = userService.getUserIdFromSession(session);
    ApiResponse<CartResponse> response = new ApiResponse<>();
    response.setResult(cartService.addToCart(userId, request));
    return response;
  }

  @PutMapping("/add/{productId}")
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