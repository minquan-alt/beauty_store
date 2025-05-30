package com.beautystore.adeline.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class PurchaseOrderUpdateRequest {
    @NotNull(message = "SUPPLIER_ID_REQUIRED")
    Long supplier_id;

    @Valid
    @NotEmpty(message = "PURCHASE_ITEMS_REQUIRED")
    List<PurchaseOrderRequest.PurchaseItem> items;
}
