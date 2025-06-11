package com.beautystore.adeline.dto.request;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PurchaseOrderRequest {
    @Valid
    @NotEmpty(message = "PURCHASE_ITEMS_REQUIRED")
    private List<PurchaseItem> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Builder
    public static class PurchaseItem {
        @NotNull(message = "PRODUCT_ID_REQUIRED")
        Long productId;

        @NotNull(message = "QUANTITY_REQUIRED")
        @Positive(message = "QUANTITY_INVALID")
        Integer quantity;

        @NotNull(message = "UNIT_PRICE_REQUIRED")
        @Positive(message = "UNIT_PRICE_INVALID")
        BigDecimal unitPrice;
    }
}
