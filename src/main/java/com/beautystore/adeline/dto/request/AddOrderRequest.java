package com.beautystore.adeline.dto.request;

import com.beautystore.adeline.entity.Order.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddOrderRequest {

    @NotNull(message = "ADDRESS_REQUIRED")
    Long addressId;

    @Valid
    @NotEmpty(message = "ORDER_ITEMS_REQUIRED")
    List<OrderItemRequest> items;

    @Valid
    @NotNull(message = "PAYMENT_INFO_REQUIRED")
    PaymentInfo paymentInfo;

    @Size(max = 500, message = "NOTES_TOO_LONG")
    String notes;

    // Nhóm thông tin thanh toán
    @Data
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PaymentInfo {
        @NotNull(message = "PAYMENT_METHOD_REQUIRED")
        PaymentMethod method;

        @PositiveOrZero(message = "SHIPPING_FEE_INVALID")
        @Builder.Default
        Double shippingFee = 2.0;

        @PositiveOrZero(message = "TAX_INVALID")
        @Builder.Default
        Double tax = 0.0;

        @Size(max = 20, message = "COUPON_CODE_TOO_LONG")
        String couponCode;
    }

    @Data
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class OrderItemRequest {
        @NotNull(message = "PRODUCT_ID_REQUIRED")
        Long productId;

        @Positive(message = "QUANTITY_INVALID")
        Integer quantity;
    }
}