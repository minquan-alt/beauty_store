package com.beautystore.adeline.repository;

import java.time.LocalDate;

import com.beautystore.adeline.dto.response.InventoryReportResultResponse;

public interface InventoryReportRepository {
    InventoryReportResultResponse generateInventoryReport(LocalDate startDate, LocalDate endDate);
}