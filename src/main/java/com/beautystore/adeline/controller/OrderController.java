package com.beautystore.adeline.controller;

import java.util.List;

import com.beautystore.adeline.dto.request.AddOrderRequest;
import com.beautystore.adeline.dto.response.ApiResponse;
import com.beautystore.adeline.dto.response.OrderResponse;
import com.beautystore.adeline.services.OrderService;
import com.beautystore.adeline.services.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;


@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final HttpSession httpSession;

    public OrderController(OrderService orderService, HttpSession httpSession, UserService userService) {
        this.orderService = orderService;
        this.httpSession = httpSession;
    }

    // Lấy tất cả đơn hàng (Admin)
    @GetMapping
    public ApiResponse<List<OrderResponse>> getAllOrders() {
        List <OrderResponse> orders = orderService.getAllOrders();
        return ApiResponse.<List<OrderResponse>> builder()
            .result(orders)
            .build();
    }

    // Lấy tất cả đơn hàng của user hiện tại
    @GetMapping("/my-orders")
    public ApiResponse<List<OrderResponse>> getMyOrders() {
        List <OrderResponse> orders = orderService.getAllMyOrders(httpSession);
        return ApiResponse.<List<OrderResponse>> builder()
            .result(orders)
            .build();
    }

    // Lấy đơn hàng hiện tại từ session
    @GetMapping("/current")
    public ApiResponse <OrderResponse> getCurrentOrder() {
        OrderResponse order = (OrderResponse) orderService.getOrderFromSession(httpSession);
        return ApiResponse.<OrderResponse> builder()
            .result(order)
            .build();
    }

    // OrderController.java
    @PostMapping("/add")
    public ApiResponse<OrderResponse> addOrder(@RequestBody AddOrderRequest request) {
        OrderResponse response = orderService.addOrder(request, httpSession);
        return ApiResponse.<OrderResponse>builder()
            .result(response)
            .build();
    }

    // @PutMapping("/cancel/{orderId}")
    // public ApiResponse<OrderResponse> cancelOrder(@PathVariable Long orderId) {
    //     OrderResponse response = orderService.cancelOrder(orderId);
    // }
}