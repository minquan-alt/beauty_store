package com.beautystore.adeline.services;

import com.beautystore.adeline.dto.request.InventoryCreateRequest;
import com.beautystore.adeline.dto.response.ApiResponse;
import com.beautystore.adeline.dto.response.InventoryReportResultResponse;
import com.beautystore.adeline.dto.response.InventoryResponse;
import com.beautystore.adeline.entity.Inventory;
import com.beautystore.adeline.exception.AppException;
import com.beautystore.adeline.exception.ErrorCode;
import com.beautystore.adeline.mapper.InventoryMapper;
import com.beautystore.adeline.repository.InventoryReportRepository;
import com.beautystore.adeline.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    @Autowired
    private InventoryReportRepository inventoryReportRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private final InventoryMapper inventoryMapper;

    public Inventory createInventory(InventoryCreateRequest request) {
        Inventory inventory = inventoryMapper.toInventory(request);
        return inventoryRepository.save(inventory);
    }

    public List<Inventory> getInventories() {
        List<Inventory> inventories = inventoryRepository.findAll();
        if (inventories.isEmpty()) {
            throw new AppException(ErrorCode.INVENTORY_LIST_EMPTY);
        }
        return inventories;
    }

    public InventoryResponse getInventory(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));
        return inventoryMapper.toInventoryResponse(inventory);
    }

    public InventoryResponse updateInventory(InventoryCreateRequest request, Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));
        inventoryMapper.updateInventory(inventory, request);
        return inventoryMapper.toInventoryResponse(inventoryRepository.save(inventory));
    }

    public void deleteInventory(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INVENTORY_NOT_FOUND));
        if (!inventory.getProducts().isEmpty()) {
            throw new AppException(ErrorCode.INVENTORY_HAS_PRODUCTS);
        }
        inventoryRepository.delete(inventory);
    }

    public InventoryReportResultResponse generateInventoryReport(LocalDate startDate, LocalDate endDate) {
        try {
            return inventoryReportRepository.generateInventoryReport(startDate, endDate);
        } catch (Exception e) {
            System.out.println(e);
            throw e;
        }
    }
}
