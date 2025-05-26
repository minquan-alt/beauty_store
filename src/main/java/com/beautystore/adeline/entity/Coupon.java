package com.beautystore.adeline.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupon", uniqueConstraints = @UniqueConstraint(columnNames = "code"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    public enum CouponType {
        PERCENTAGE,    // Giảm giá theo phần trăm
        FIXED_AMOUNT,  // Giảm giá cố định
        FREE_SHIPPING  // Miễn phí vận chuyển
    }

    @Id
    @Column(nullable = false, length = 50)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_type", nullable = false)
    private CouponType type;

    @DecimalMin("0.00")
    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal value;  

    @DecimalMin("0.00")
    @Column(name = "min_order_amount", precision = 10, scale = 2)
    private BigDecimal minOrderAmount; 

    @DecimalMin("0.00")
    @Column(name = "max_discount_amount", precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount; 

    @NotNull
    @Column(name = "start_date")
    private LocalDate startDate;  

    @NotNull
    @Column(name = "expiration_date")
    private LocalDate expirationDate; 

    @PositiveOrZero
    @Column(name = "usage_limit")
    private Integer usageLimit; 

    @PositiveOrZero
    @Column(name = "used_count", nullable = false)
    @Builder.Default
    private Integer usedCount = 0; 

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicable_user_id", foreignKey = @ForeignKey(name = "fk_applicable_user"))
    private User applicableUser;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (usedCount == null) {
            usedCount = 0;
        }
        if (active == null) {
            active = true;
        }
    }

    public boolean isValid() {
        LocalDate today = LocalDate.now();
        return active && 
               !today.isBefore(startDate) && 
               !today.isAfter(expirationDate) &&
               (usageLimit == null || usedCount < usageLimit);
    }

    public boolean isApplicableFor(User user) {
        return applicableUser == null || applicableUser.equals(user);
    }
}