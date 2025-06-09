package com.beautystore.adeline.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddOrderRequest {

    @NotNull(message = "ADDRESS_REQUIRED")
    Long addressId;

    @Size(max = 500, message = "NOTES_TOO_LONG")
    String notes;

    @Valid
    @NotNull(message = "PAYMENT_INFO_REQUIRED")
    PaymentInfo paymentInfo;

    @Data
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PaymentInfo {
        @PositiveOrZero(message = "SHIPPING_FEE_INVALID")
        @Builder.Default
        Double shippingFee = 2.0;

        @PositiveOrZero(message = "TAX_INVALID")
        @Builder.Default
        Double tax = 0.0;

        @Size(max = 20, message = "COUPON_CODE_TOO_LONG")
        String couponCode;
    }
}