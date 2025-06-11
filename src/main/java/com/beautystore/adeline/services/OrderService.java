package com.beautystore.adeline.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.beautystore.adeline.dto.request.AddOrderRequest;
import com.beautystore.adeline.dto.request.OrderUpdateRequest;
import com.beautystore.adeline.dto.response.CartItemResponse;
import com.beautystore.adeline.dto.response.DashboardDataResponse;
import com.beautystore.adeline.dto.response.OrderResponse;
import com.beautystore.adeline.dto.response.FinancialMetricsResponse;
import com.beautystore.adeline.entity.Address;
import com.beautystore.adeline.entity.Coupon;
import com.beautystore.adeline.entity.Order;
import com.beautystore.adeline.entity.Order.OrderStatus;
import com.beautystore.adeline.entity.OrderDetail;
import com.beautystore.adeline.entity.Product;
import com.beautystore.adeline.entity.PurchaseOrder;
import com.beautystore.adeline.entity.User;
import com.beautystore.adeline.exception.AppException;
import com.beautystore.adeline.exception.ErrorCode;
import com.beautystore.adeline.repository.AddressRepository;
import com.beautystore.adeline.repository.CouponRepository;
import com.beautystore.adeline.repository.InventoryRepository;
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
    private final InventoryRepository inventoryRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository, UserService userService, UserRepository userRepository, AddressRepository addressRepository, ProductRepository productRepository, CouponRepository couponRepository,InventoryRepository inventoryRepository, PurchaseOrderRepository purchaseOrderRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.couponRepository = couponRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.inventoryRepository = inventoryRepository;
        this.cartService = cartService;
    }

    public DashboardDataResponse getDashboardData(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = getOrdersByDateRange(startDate, endDate);

        int totalOrders = orders.size();
        BigDecimal totalRevenue = getRealRevenue(orders);
        BigDecimal totalCost = getTotalCost(orders);
        BigDecimal totalProfit = totalRevenue.subtract(totalCost);

        // Chart data: date -> revenue, date -> profit
        Map<String, Object> chartData = new HashMap<>();
        //Map<String, BigDecimal> revenueByDate = new TreeMap<>();
        Map<String, BigDecimal> profitByDate = new TreeMap<>();

        for (Order order : orders) {
            String date = order.getOrderDate().toLocalDate().toString();
            BigDecimal orderRevenue = order.getTotalAmount();
            BigDecimal orderCost = getTotalCost(List.of(order));
            BigDecimal orderProfit = orderRevenue.subtract(orderCost);
            //revenueByDate.merge(date, orderRevenue, BigDecimal::add);
            profitByDate.merge(date, orderProfit, BigDecimal::add);
        }

        //chartData.put("revenue", revenueByDate);
        chartData.put("profit", profitByDate);

        return DashboardDataResponse.builder()
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .totalProfit(totalProfit)
                .chartData(chartData)
                .build();
    }

    public class TempOrderSession {
        private Long orderId;
        private long expiryTime;

        public TempOrderSession(Long orderId, long expiryTime) {
            this.orderId = orderId;
            this.expiryTime = expiryTime;
        }

        public Long getOrderId() {
            return orderId;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }


    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public OrderResponse updateOrder(OrderUpdateRequest request ,Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        order.setStatus(OrderStatus.valueOf(request.getStatus()));
        return mapToResponse(orderRepository.save(order));
    }

    public List<OrderResponse> getAllMyOrders(HttpSession session) {
        Long userId = userService.getUserIdFromSession(session);
        if (userId == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        return orderRepository.findByUserId(userId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public OrderResponse getOrderFromSession(HttpSession session) {
        TempOrderSession temp = (TempOrderSession) session.getAttribute("orderSession");
        if (temp == null) {
            throw new AppException(ErrorCode.ORDER_NOT_IN_SESSION);
        }
        Long orderId = (Long) temp.getOrderId();

        if (orderId == null) {
            throw new AppException(ErrorCode.ORDER_ID_NOT_IN_SESSION);
        }
        return orderRepository.findById(orderId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
    }

    @Transactional
    public OrderResponse addOrder(AddOrderRequest request, HttpSession session) {
        // 1. Xác thực người dùng
        Long userId = (Long) session.getAttribute("userId");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 2. Validate address
        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        // 3. Tạo order entity
        Order order = new Order();
        order.setUser(user);

        // 4. Xử lý order items
        List<OrderDetail> orderItems = cartService.getCart(userId).getItems().stream()
            .map(cartItem -> {
                Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
                
                int product_quantity = product.getQuantity();
                if (cartItem.getQuantity() > product_quantity) {
                    throw new AppException(ErrorCode.STOCK_QUANTITY_NOT_ENOUGH);
                }

                return OrderDetail.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();
            })
            .collect(Collectors.toList());

        // List<OrderDetail> orderItems = request.getItems().stream()
        //         .map((AddOrderRequest.OrderItemRequest itemRequest) -> { // Thêm explicit type
        //             Product product = productRepository.findById(itemRequest.getProductId())
        //                     .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        //         int product_quantity =  product.getQuantity();
                
        //         if(itemRequest.getQuantity() > product_quantity) {
        //             throw new AppException(ErrorCode.STOCK_QUANTITY_NOT_ENOUGH);
        //         }

        //         return OrderDetail.builder()
        //                 .order(order)
        //                 .product(product)
        //                 .quantity(itemRequest.getQuantity())
        //                 .unitPrice(product.getPrice())
        //                 .build();
        //     })
        // .collect(Collectors.toList());

        order.setItems(orderItems);

        // 5. Tính toán tổng tiền
        BigDecimal subtotal = orderItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setAddress(address);
        order.setSubtotal(subtotal);
        order.setNotes(request.getNotes());
        order.setShippingFee(BigDecimal.valueOf( request.getPaymentInfo().getShippingFee() != null ? request.getPaymentInfo().getShippingFee() : 2.0 ));
        order.setTax(BigDecimal.valueOf(request.getPaymentInfo().getTax() != null ? request.getPaymentInfo().getTax() : 0.0));

        // 6. Xử lý coupon
        if (StringUtils.hasText(request.getPaymentInfo().getCouponCode())) {
            applyCouponToOrder(order, request.getPaymentInfo().getCouponCode());
        } else {
            order.calculateTotal();
        }

        // 7. Lưu order
        Order savedOrder = orderRepository.save(order);
        OrderResponse response = mapToResponse(savedOrder);

        long expiryTime = System.currentTimeMillis() + 15 * 60 * 1000;
        TempOrderSession temp = new TempOrderSession(savedOrder.getId(), expiryTime);
        session.setAttribute("orderSession", temp);
        session.setAttribute("order", response);
        // 8. Map sang DTO
        return response;
    }

    private OrderResponse mapToResponse(Order order) {
        String address = "";
        address = order.getAddress().getCountry() + ", " + order.getAddress().getCity() + ", " + order.getAddress().getStreet();
        return OrderResponse.builder()
                .orderId(order.getId())
                .customerName(order.getUser().getName())
                .orderDate(order.getOrderDate())
                .subTotal(order.getSubtotal())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .discount(order.getDiscount())
                .shippingFee(order.getShippingFee())
                .phone(order.getUser().getPhone())
                .address(address)
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

        Coupon coupon = couponRepository.findById(couponCode)
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

    public List<PurchaseOrder> getCompletedPurchaseOrdersByOrderId(Order order) {
        return purchaseOrderRepository.findByOrderIdAndStatus(order.getId(), PurchaseOrder.Status.Completed);
    }

    // Giá đã giảm
    public BigDecimal getRealRevenue(List<Order> orders) {
        return orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.Confirmed)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Giá gốc
    public BigDecimal getOriginalRevenue(List<Order> orders) {
        return orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.Confirmed)
                .map(Order::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalCost(List<Order> orders) {
        return orders.stream()
            .flatMap(order -> getCompletedPurchaseOrdersByOrderId(order).stream())
            .flatMap(po -> po.getOrderDetails().stream())
            .map(pod -> pod.getUnitPrice())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<PurchaseOrder> getPurchaseOrderByOrderId(Order order) {
        return purchaseOrderRepository.findByOrderId(order.getId());
    }

    public BigDecimal getTotalShippingFee(List<Order> orders){
        return orders.stream()
            .filter(order -> order.getStatus() == OrderStatus.Confirmed)
            .map(Order::getShippingFee)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalDiscount(List<Order> orders){
        return orders.stream()
            .filter(order -> order.getStatus() == OrderStatus.Confirmed)
            .map(Order::getDiscount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Map<String, Object>> getProfitChartData(LocalDateTime start, LocalDateTime end) {
        var dashboardData = getDashboardData(start, end);
        Map<String, BigDecimal> profitMap = (Map<String, BigDecimal>) dashboardData.getChartData().get("profit");
        List<Map<String, Object>> chartData = new ArrayList<>();
        for (String date : profitMap.keySet()) {
            Map<String, Object> point = new HashMap<>();
            point.put("label", date);
            point.put("value", profitMap.get(date) != null ? profitMap.get(date) : BigDecimal.ZERO);
            chartData.add(point);
        }
        return chartData;
    }

    @Transactional
    public FinancialMetricsResponse getFinancialMetrics(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = getOrdersByDateRange(startDate, endDate);
        
        return FinancialMetricsResponse.builder()
                .totalOrders(orders.size())
                .totalProfit(getRealRevenue(orders).subtract(getTotalCost(orders)))
                .totalCost(getTotalCost(orders))
                .totalShippingFee(getTotalShippingFee(orders))
                .totalDiscount(getTotalDiscount(orders))
                .totalRealRevenue(getRealRevenue(orders))
                .totalOriginalRevenue(getOriginalRevenue(orders))
                .build();
    }
}
