package com.beautystore.adeline.dto.request;


import com.beautystore.adeline.entity.Order.PaymentMethod;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddPaymentRequest {
    String transactionId;
    PaymentMethod paymentMethod;
}
