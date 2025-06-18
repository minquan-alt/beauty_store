package com.beautystore.adeline.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReportResponse {
    private Long productId;
    private String name;
    private Long totalQuantity;
    private BigDecimal totalAmount;

    @Override
    public String toString() {
        return "InventoryReportDTO{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", totalQuantity=" + totalQuantity +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
