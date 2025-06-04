package com.beautystore.adeline.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class DashboardDataResponse {
    private int totalOrders;
    private BigDecimal totalRevenue;
    private BigDecimal totalProfit;
    private Map<String, Object> chartData;
}