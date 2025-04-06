package com.beautystore.adeline.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.beautystore.adeline.dto.request.ProductCreateRequest;
import com.beautystore.adeline.entity.Product;
import com.beautystore.adeline.repository.CategoryRepository;
import com.beautystore.adeline.repository.SupplierRepository;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {
    
    @Autowired
    protected CategoryRepository categoryRepository;
    
    @Autowired
    protected SupplierRepository supplierRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", expression = "java(categoryRepository.getReferenceById(request.getCategory_id()))")
    @Mapping(target = "supplier", expression = "java(supplierRepository.getReferenceById(request.getSupplier_id()))")
    public abstract Product toProduct(ProductCreateRequest request);
}