package com.beautystore.adeline.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.beautystore.adeline.dto.request.SupplierCreateRequest;
import com.beautystore.adeline.dto.response.SupplierResponse;
import com.beautystore.adeline.entity.Supplier;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    Supplier toSupplier(SupplierCreateRequest Request);
    SupplierResponse toSupplierResponse(Supplier supplier);
    void updateSupplier(@MappingTarget Supplier supplier, SupplierCreateRequest request);
}
