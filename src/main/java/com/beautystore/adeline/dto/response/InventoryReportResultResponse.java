package com.beautystore.adeline.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReportResultResponse {
    private List<InventoryReportResponse> openingInventory;
    private List<InventoryReportResponse> incomingInventory;
    private List<InventoryReportResponse> outgoingInventory;
    private List<InventoryReportResponse> closingInventory;
}