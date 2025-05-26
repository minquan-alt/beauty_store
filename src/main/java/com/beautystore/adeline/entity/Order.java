package com.beautystore.adeline.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class Order {

    public enum OrderStatus {
        Pending,       // Đơn mới tạo
        Confirmed,     // Đã xác nhận
        Processing,    // Đang xử lý
        Shipped,      // Đã giao cho DVVC
        Delivered,    // Giao hàng thành công
        Cancelled,    // Đã hủy
        Returned,      // Đã trả hàng
    }

    public enum PaymentMethod {
        Cash,           // Thanh toán khi nhận hàng
        Momo,           // Momo
        BankTransfer,   // Chuyển khoản
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_code")
    private Coupon coupon;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.Pending;

    @Column(name = "order_date", updatable = false, nullable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "shipping_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal tax = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 20)
    private PaymentMethod paymentMethod;

    @Lob
    private String notes;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> items = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;

    @PrePersist
    protected void onCreate() {
        if (this.orderDate == null) {
            this.orderDate = LocalDateTime.now();
        }
        calculateTotal();
    }

    public void calculateTotal() {
        if (subtotal == null) subtotal = BigDecimal.ZERO;
        if (discount == null) discount = BigDecimal.ZERO;
        if (shippingFee == null) shippingFee = BigDecimal.ZERO;
        if (tax == null) tax = BigDecimal.ZERO;

        this.totalAmount = subtotal
                .subtract(discount)
                .add(shippingFee)
                .add(tax);
    }

    // Helper method
    public void addOrderItem(OrderDetail item) {
        items.add(item);
        item.setOrder(this);
    }
}