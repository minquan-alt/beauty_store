package com.beautystore.adeline.services;

import com.beautystore.adeline.dto.request.AddToCartRequest;
import com.beautystore.adeline.dto.request.UpdateCartItemRequest;
import com.beautystore.adeline.dto.response.CartResponse;
import com.beautystore.adeline.entity.*;
import com.beautystore.adeline.exception.AppException;
import com.beautystore.adeline.exception.ErrorCode;
import com.beautystore.adeline.mapper.CartMapper;
import com.beautystore.adeline.repository.CartRepository;
import com.beautystore.adeline.repository.ProductRepository;
import com.beautystore.adeline.repository.UserRepository;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {
  private final CartRepository cartRepository;
  private final UserRepository userRepository;
  private final CartMapper cartMapper;
  private final ProductRepository productRepository;

  public CartService(CartRepository cartRepository,
      UserRepository userRepository,
      CartMapper cartMapper,
      ProductRepository productRepository) {
    this.cartRepository = cartRepository;
    this.userRepository = userRepository;
    this.cartMapper = cartMapper;
    this.productRepository = productRepository;
  }

  public CartResponse getCart(Long userId) {
    Optional<Cart> cart = cartRepository.findByUserId(userId);
    if (cart.isEmpty()) {
      Cart newCart = new Cart();
      newCart.setUser(userRepository.findById(userId)
          .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
      cartRepository.save(newCart);
      return buildCartResponse(newCart);
    }
    return buildCartResponse(cart.get());
  }

  private CartResponse buildCartResponse(Cart cart) {
    CartResponse response = cartMapper.toCartResponse(cart);
    response.setCartId(cart.getId());
    response.setUserId(cart.getUser().getId());
    response.setItems(cart.getCartItems().stream()
        .map(cartItem -> cartMapper.toCartItemResponse(cartItem))
        .collect(Collectors.toList()));
    response.setTotalCartPrice(calculateTotal(cart));
    response.setTotalItems(calculateTotalItems(cart));
    return response;
  }

  @Transactional
  public CartResponse addToCart(Long userId, AddToCartRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

    Product product = productRepository.findById(request.getProductId())
        .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

    Cart cart = cartRepository.findByUserId(userId)
        .orElseGet(() -> {
          Cart newCart = new Cart();
          newCart.setUser(user);
          return cartRepository.save(newCart);
        });

    CartItem cartItem = cart.getCartItems().stream()
        .filter(item -> item.getProduct().getId().equals(request.getProductId()))
        .findFirst()
        .orElseGet(() -> {
          CartItem newItem = new CartItem();
          newItem.setCart(cart);
          newItem.setProduct(product);
          cart.getCartItems().add(newItem);
          return newItem;
        });

    cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
    Cart savedCart = cartRepository.save(cart);

    return buildCartResponse(savedCart);
  }

  @Transactional
  public CartResponse updateCartItem(Long userId, Long productId, UpdateCartItemRequest request) {
    Cart cart = cartRepository.findByUserId(userId)
        .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

    Optional<CartItem> cartItem = cart.getCartItems().stream()
        .filter(item -> item.getProduct().getId().equals(productId))
        .findFirst();

    if (cartItem.isEmpty()) {
      CartItem newItem = new CartItem();
      newItem.setCart(cart);
      newItem.setProduct(productRepository.findById(productId)
          .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND)));
      newItem.setQuantity(request.getQuantity());
      cart.getCartItems().add(newItem);
      Cart savedCart = cartRepository.save(cart);
      return buildCartResponse(savedCart);
    }

    if (request.getQuantity() == 0) {
      cart.getCartItems().remove(cartItem.get());
    } else {
      cartItem.get().setQuantity(cartItem.get().getQuantity() + request.getQuantity());
    }

    Cart savedCart = cartRepository.save(cart);
    return buildCartResponse(savedCart);
  }

  @Transactional
  public CartResponse removeFromCart(Long userId, Long productId) {
    Cart cart = cartRepository.findByUserId(userId)
        .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

    boolean removed = cart.getCartItems().removeIf(
        item -> item.getProduct().getId().equals(productId));

    if (!removed) {
      throw new AppException(ErrorCode.CART_ITEM_NOT_FOUND);
    }

    Cart savedCart = cartRepository.save(cart);
    return buildCartResponse(savedCart);
  }

  @Transactional
  public void clearCart(Long userId) {
    Cart cart = cartRepository.findByUserId(userId)
        .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));

    if (cart.getCartItems().isEmpty()) {
      cartRepository.delete(cart);
    } else {
      cart.getCartItems().clear();
      cartRepository.save(cart);
    }

  }

  private Double calculateTotal(Cart cart) {
      return cart.getCartItems().stream()
          .mapToDouble(item -> item.getQuantity() * item.getProduct().getPrice().doubleValue())
          .sum();
  }
  
  private Integer calculateTotalItems(Cart cart) {
    return cart.getCartItems().stream()
        .mapToInt(CartItem::getQuantity)
        .sum();
  }
}