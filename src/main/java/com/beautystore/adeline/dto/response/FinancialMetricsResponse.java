package com.beautystore.adeline.dto.response;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FinancialMetricsResponse {
    private int totalOrders;
    private BigDecimal totalProfit;
    private BigDecimal totalCost;
    private BigDecimal totalShippingFee;
    private BigDecimal totalDiscount;
    private BigDecimal totalRealRevenue;
    private BigDecimal totalOriginalRevenue;
} 