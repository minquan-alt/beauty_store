package com.beautystore.adeline.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.beautystore.adeline.dto.request.ProductCreateRequest;
import com.beautystore.adeline.entity.Product;
import com.beautystore.adeline.entity.ProductImage;
import com.beautystore.adeline.repository.CategoryRepository;
import com.beautystore.adeline.repository.SupplierRepository;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {

    @Autowired
    protected CategoryRepository categoryRepository;
    @Autowired
    protected SupplierRepository supplierRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category",
             expression = "java(categoryRepository.getReferenceById(request.getCategory_id()))")
    @Mapping(target = "supplier",
             expression = "java(supplierRepository.getReferenceById(request.getSupplier_id()))")
    @Mapping(target = "images", ignore = true)   
    public abstract Product toProduct(ProductCreateRequest request);

    @AfterMapping
    protected void mapImages(ProductCreateRequest request,
                              @MappingTarget Product product) {

        List<String> urls = request.getImageUrls();
        if (urls == null || urls.isEmpty()) return;

        List<ProductImage> images = urls.stream()
            .filter(url -> url != null && !url.isBlank())
            .map(url -> {
                ProductImage img = new ProductImage();
                img.setImageUrl(url.trim());
                img.setProduct(product);       
                return img;
            })
            .collect(Collectors.toList());

        product.setImages(images);
    }
}
