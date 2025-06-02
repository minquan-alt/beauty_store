package com.beautystore.adeline.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardDataResponse {
    private int totalOrders;
    private int totalProductsSold;
    private BigDecimal totalAmount;
    private BigDecimal totalSubTotal;
    private BigDecimal totalCost;
    private BigDecimal totalProfit;
    private Map<String, Object> chartData;
}