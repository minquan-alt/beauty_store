package com.beautystore.adeline.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.beautystore.adeline.dto.request.AddOrderRequest;
import com.beautystore.adeline.dto.response.DashboardDataResponse;
import com.beautystore.adeline.dto.response.OrderResponse;
import com.beautystore.adeline.entity.Address;
import com.beautystore.adeline.entity.Coupon;
import com.beautystore.adeline.entity.Order;
import com.beautystore.adeline.entity.OrderDetail;
import com.beautystore.adeline.entity.Product;
import com.beautystore.adeline.entity.PurchaseOrder;
import com.beautystore.adeline.entity.User;
import com.beautystore.adeline.exception.AppException;
import com.beautystore.adeline.exception.ErrorCode;
import com.beautystore.adeline.repository.AddressRepository;
import com.beautystore.adeline.repository.CouponRepository;
import com.beautystore.adeline.repository.OrderRepository;
import com.beautystore.adeline.repository.ProductRepository;
import com.beautystore.adeline.repository.PurchaseOrderRepository;
import com.beautystore.adeline.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    public OrderService(OrderRepository orderRepository, UserService userService, UserRepository userRepository,
            AddressRepository addressRepository, ProductRepository productRepository,
            CouponRepository couponRepository,
            PurchaseOrderRepository purchaseOrderRepository) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.couponRepository = couponRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    public DashboardDataResponse getDashboardData(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = getOrdersByDateRange(startDate, endDate);

        int totalOrders = orders.size();
        int totalProductsSold = getTotalProductsSoldByDateRange(startDate, endDate);
        BigDecimal totalAmount = getRealRevenue(orders);
        BigDecimal totalSubTotal = getOriginalRevenue(orders);
        BigDecimal totalCost = getTotalCost(orders);
        BigDecimal totalProfit = totalAmount.subtract(totalCost);

        Map<LocalDate, BigDecimal> dailyTotalAmount = new TreeMap<>();
        Map<LocalDate, BigDecimal> dailySubTotal = new TreeMap<>();
        Map<LocalDate, BigDecimal> dailyTotalProfit = new TreeMap<>();

        for (Order order : orders) {
            LocalDate orderDay = order.getOrderDate().toLocalDate();
            dailyTotalAmount.merge(orderDay, order.getTotalAmount(), BigDecimal::add);
            BigDecimal orderCost = getTotalCost(List.of(order));
            BigDecimal orderProfit = order.getTotalAmount().subtract(orderCost);
            dailySubTotal.merge(orderDay, order.getSubtotal(), BigDecimal::add);
            dailyTotalProfit.merge(orderDay, orderProfit, BigDecimal::add);
        }

        List<String> dates = dailyTotalAmount.keySet().stream().map(LocalDate::toString).collect(Collectors.toList());
        List<BigDecimal> chartTotalAmount = new ArrayList<>(dailyTotalAmount.values());
        List<BigDecimal> chartSubTotal = new ArrayList<>(dailySubTotal.values());
        List<BigDecimal> chartTotalProfit = new ArrayList<>(dailyTotalProfit.values());

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("dates", dates);
        chartData.put("chartTotalAmount", chartTotalAmount);
        chartData.put("chartSubTotal", chartSubTotal);
        chartData.put("chartTotalProfit", chartTotalProfit);

        return DashboardDataResponse.builder()
                .totalOrders(totalOrders)
                .totalProductsSold(totalProductsSold)
                .totalAmount(totalAmount)
                .totalSubTotal(totalSubTotal)
                .totalCost(totalCost)
                .totalProfit(totalProfit)
                .chartData(chartData)
                .build();
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<OrderResponse> getAllMyOrders(HttpSession session) {
        Long userId = userService.getUserIdFromSession(session);
        if (userId == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public OrderResponse getOrderFromSession(HttpSession session) {
        Long orderId = (Long) session.getAttribute("orderId");
        if (orderId == null) {
            throw new AppException(ErrorCode.ORDER_NOT_IN_SESSION);
        }
        return orderRepository.findById(orderId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
    }

    @Transactional
    public OrderResponse addOrder(AddOrderRequest request, HttpSession session) {
        // 1. Xác thực người dùng
        Long userId = userService.getUserIdFromSession(session);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 2. Validate address
        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        // 3. Tạo order entity
        Order order = new Order();
        order.setUser(user);

        // 4. Xử lý order items
        List<OrderDetail> orderItems = request.getItems().stream()
                .map((AddOrderRequest.OrderItemRequest itemRequest) -> { // Thêm explicit type
                    Product product = productRepository.findById(itemRequest.getProductId())
                            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

                    return OrderDetail.builder()
                            .order(order)
                            .product(product)
                            .quantity(itemRequest.getQuantity())
                            .unitPrice(product.getPrice())
                            .build();
                })
                .collect(Collectors.toList());

        order.setItems(orderItems);

        // 5. Tính toán tổng tiền
        BigDecimal subtotal = orderItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setAddress(address);
        order.setSubtotal(subtotal);
        order.setPaymentMethod(request.getPaymentInfo().getMethod());
        order.setNotes(request.getNotes());
        order.setShippingFee(BigDecimal.valueOf(request.getPaymentInfo().getShippingFee()));
        order.setTax(BigDecimal.valueOf(request.getPaymentInfo().getTax()));

        // 6. Xử lý coupon
        if (StringUtils.hasText(request.getPaymentInfo().getCouponCode())) {
            applyCouponToOrder(order, request.getPaymentInfo().getCouponCode());
        } else {
            order.calculateTotal();
        }

        // 7. Lưu order
        Order savedOrder = orderRepository.save(order);
        session.setAttribute("orderId", savedOrder.getId());

        // 8. Map sang DTO
        return mapToResponse(savedOrder);
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .orderDate(order.getOrderDate())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .items(order.getItems().stream()
                        .map(this::mapToItemResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    private OrderResponse.OrderItemResponse mapToItemResponse(OrderDetail item) {
        return OrderResponse.OrderItemResponse.builder()
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .price(item.getUnitPrice())
                .build();
    }

    @Transactional
    public Order applyCouponToOrder(Order order, String couponCode) {
        if (couponCode == null || couponCode.isEmpty()) {
            order.setCoupon(null);
            order.setDiscount(BigDecimal.ZERO);
            order.calculateTotal();
            return order;
        }

        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_FOUND));

        // Kiểm tra coupon còn hiệu lực
        if (LocalDate.now().isAfter(coupon.getExpirationDate())) {
            throw new AppException(ErrorCode.COUPON_EXPIRED);
        }

        // Kiểm tra coupon có áp dụng cho đơn hàng này không
        if (coupon.getMinOrderAmount() != null &&
                order.getSubtotal().compareTo(coupon.getMinOrderAmount()) < 0) {
            throw new AppException(ErrorCode.COUPON_MIN_ORDER_NOT_MET);
        }

        // Tính toán giảm giá theo loại coupon
        BigDecimal discountAmount;
        if (coupon.getType() == Coupon.CouponType.PERCENTAGE) {
            discountAmount = order.getSubtotal()
                    .multiply(coupon.getValue().divide(BigDecimal.valueOf(100)));

            if (coupon.getMaxDiscountAmount() != null) {
                discountAmount = discountAmount.min(coupon.getMaxDiscountAmount());
            }
        } else {
            discountAmount = coupon.getValue();
        }

        // Áp dụng vào đơn hàng
        order.setCoupon(coupon);
        order.setDiscount(discountAmount);
        order.calculateTotal();

        return order;
    }

    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByOrderDateBetween(startDate, endDate);
    }

    public int getTotalProductsSoldByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByOrderDateBetween(startDate, endDate).stream()
                .flatMap(order -> order.getItems().stream())
                .mapToInt(OrderDetail::getQuantity)
                .sum();
    }

    // Giá đã giảm
    public BigDecimal getRealRevenue(List<Order> orders) {
        return orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Giá gốc
    public BigDecimal getOriginalRevenue(List<Order> orders) {
        return orders.stream()
                .map(Order::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalCost(List<Order> orders) {
        return orders.stream()
                .flatMap(order -> getPurchaseOrderByOrderId(order).stream())
                .flatMap(po -> po.getOrderDetails().stream())
                .map(pod -> pod.getUnitPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<PurchaseOrder> getPurchaseOrderByOrderId(Order order) {
        return purchaseOrderRepository.findByOrderId(order.getId());
    }
}
