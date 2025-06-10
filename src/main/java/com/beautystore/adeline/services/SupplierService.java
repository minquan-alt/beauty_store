package com.beautystore.adeline.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beautystore.adeline.dto.request.SupplierCreateRequest;
import com.beautystore.adeline.dto.response.SupplierResponse;
import com.beautystore.adeline.entity.Supplier;
import com.beautystore.adeline.exception.AppException;
import com.beautystore.adeline.exception.ErrorCode;
import com.beautystore.adeline.mapper.SupplierMapper;
import com.beautystore.adeline.repository.SupplierRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupplierService {
    
    private static Logger logger = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private final SupplierMapper supplierMapper;

    public Supplier createSupplier(SupplierCreateRequest request){
        Supplier supplier = supplierMapper.toSupplier(request);
        return supplierRepository.save(supplier);
    }

    public List<Supplier> getSuppliers(){
        List<Supplier> suppliers = supplierRepository.findAll();
        if(suppliers.isEmpty())
            throw new AppException(ErrorCode.SUPPLIER_LIST_EMPTY);
        return suppliers;
    }

    public SupplierResponse getSupplier(Long id){
        logger.info("Fetching supplier with id: {}", id); 
        Supplier supplier = supplierRepository.findById(id)
            .orElseThrow(()  -> {
                return new AppException(ErrorCode.SUPPLIER_NOT_FOUND);
            });
            
        logger.info("Category found: {}", supplier);
        return supplierMapper.toSupplierResponse(supplier);
    }

    public SupplierResponse updateSupplier(SupplierCreateRequest request, Long id){
        Supplier supplier = supplierRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));

        supplierMapper.updateSupplier(supplier, request);
        return supplierMapper.toSupplierResponse(supplierRepository.save(supplier));
    }

    public void deleteSupplier(Long id){
        Supplier supplier = supplierRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.SUPPLIER_NOT_FOUND));
        
        if(!supplier.getProducts().isEmpty()){
            throw new AppException(ErrorCode.SUPPLIER_HAS_PRODUCT);
        }

        supplierRepository.delete(supplier);
    }
}
