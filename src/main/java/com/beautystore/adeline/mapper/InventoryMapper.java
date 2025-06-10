package com.beautystore.adeline.mapper;

import com.beautystore.adeline.dto.request.InventoryCreateRequest;
import com.beautystore.adeline.dto.response.InventoryResponse;
import com.beautystore.adeline.entity.Inventory;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InventoryMapper {
    Inventory toInventory(InventoryCreateRequest request);
    InventoryResponse toInventoryResponse(Inventory inventory);
    void updateInventory(@MappingTarget Inventory inventory, InventoryCreateRequest request);
}
