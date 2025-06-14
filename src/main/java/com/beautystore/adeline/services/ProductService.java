package com.beautystore.adeline.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beautystore.adeline.dto.request.ProductCreateRequest;
import com.beautystore.adeline.dto.request.ProductUpdateRequest;
import com.beautystore.adeline.dto.response.GetProductImageResponse;
import com.beautystore.adeline.dto.response.ProductImageResponse;
import com.beautystore.adeline.dto.response.ProductResponse;
import com.beautystore.adeline.entity.Product;
import com.beautystore.adeline.entity.ProductImage;
import com.beautystore.adeline.exception.AppException;
import com.beautystore.adeline.exception.ErrorCode;
import com.beautystore.adeline.mapper.ProductMapper;
import com.beautystore.adeline.mapper.ProductResponseMapper;
import com.beautystore.adeline.repository.CategoryRepository;
import com.beautystore.adeline.repository.InventoryRepository;
import com.beautystore.adeline.repository.ProductRepository;
import com.beautystore.adeline.repository.SupplierRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductResponseMapper productResponseMapper;
    private final ProductMapper productMapper;

    @PersistenceContext
    private EntityManager entityManager;

    public ProductResponse createProduct(ProductCreateRequest request) {
        if (!supplierRepository.existsById(request.getSupplier_id())) {
            throw new AppException(ErrorCode.SUPPLIER_NOT_FOUND);
        }

        if (!categoryRepository.existsById(request.getCategory_id())) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        if(!inventoryRepository.existsById(request.getInventory_id())){
            throw new AppException(ErrorCode.INVENTORY_NOT_FOUND);
        }

        Product product = productMapper.toProduct(request);
        Product savedProduct = productRepository.save(product);
        return productResponseMapper.toResponse(savedProduct);
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        System.out.println("Tất cả product: " + products);
        if (products.isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_LIST_EMPTY);
        }

        return products.stream()
                .map(productResponseMapper::toResponse)
                .toList();
    };

    public int countProducts() {
        return (int) productRepository.count();
    }

    public Page<ProductResponse> getProductsPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(productResponseMapper::toResponse);
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    return new AppException(ErrorCode.USER_NOT_FOUND);
                });

        return productResponseMapper.toResponse(product);
    };

    @Transactional
    public ProductResponse updateProduct(ProductUpdateRequest request, Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        productResponseMapper.updateProduct(product, request);

        if (request.getPrice().compareTo(product.getPrice()) < 0) {
            throw new AppException(ErrorCode.PRODUCT_PRICE_LOWER_THAN_COST);
        }

        if (request.getCategory_id() != null)
            product.setCategory(categoryRepository.getReferenceById(request.getCategory_id()));

        if (request.getSupplier_id() != null)
            product.setSupplier(supplierRepository.getReferenceById(request.getSupplier_id()));

        if (request.getInventory_id() != null)
            product.setInventory(inventoryRepository.getReferenceById(request.getInventory_id()));

        refreshImages(product, request.getImageUrls());

        productRepository.save(product);
        return productResponseMapper.toResponse(product);
    }

    private void refreshImages(Product product, List<String> newUrls) {

        product.getImages().clear();

        if (newUrls == null || newUrls.isEmpty()) return;

        newUrls.stream()
               .filter(url -> url != null && !url.isBlank())
               .map(String::trim)
               .forEach(url -> {
                   ProductImage img = new ProductImage();
                   img.setImageUrl(url);
                   img.setProduct(product);          
                   product.getImages().add(img);
               });
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    };

    public Page<ProductResponse> searchProducts(String keyword,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            List<String> categories, int page, int size) {

        String searchedKeyword = null;
        if (keyword != null && !keyword.trim().isEmpty()) {
            searchedKeyword = "%" + keyword.toLowerCase() + "%";
        }
        List<String> searchedCategories = null;
        if (categories != null && !categories.isEmpty()) {
            searchedCategories = categories.stream().map(String::toLowerCase).toList();
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Product> productPage;

        if (searchedKeyword == null && (searchedCategories == null || searchedCategories.isEmpty())) {
            //Hận thằng js -_-
            productPage = productRepository.searchByPriceOnly(minPrice, maxPrice, pageable);
        } else {
            productPage = productRepository.searchProducts(searchedKeyword, minPrice, maxPrice,
                    searchedCategories, pageable);
        }

        return productPage.map(productResponseMapper::toResponse);

    }

    public GetProductImageResponse getProductImages(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<ProductImageResponse> imageRes = product.getImages().stream()
                .map(img -> ProductImageResponse.builder()
                        .id(img.getId())
                        .imageUrl(img.getImageUrl())
                        .build())
                .toList();

        return GetProductImageResponse.builder()
                .images(imageRes)
                .build();
    }

    public Page<ProductResponse> getProductsPageWithFilterSort(int page, int size, String name, Long categoryId, String sortParam) {
        String baseQuery = "FROM Product p WHERE 1=1";

        Map<String, Object> params = new HashMap<>();

        if (name != null && !name.isBlank()) {
            baseQuery += " AND LOWER(p.name) LIKE :name";
            params.put("name", "%" + name.toLowerCase() + "%");
        }

        if (categoryId != null) {
            baseQuery += " AND p.category.id = :categoryId";
            params.put("categoryId", categoryId);
        }

        String orderBy = "";
        if (sortParam != null && !sortParam.isBlank()) {
            boolean desc = sortParam.startsWith("-");
            String field = desc ? sortParam.substring(1) : sortParam;

            if (field.equalsIgnoreCase("price") || field.equalsIgnoreCase("name")) {
                orderBy = " ORDER BY p." + field + (desc ? " DESC" : " ASC");
            }
        }

        String finalDataQuery = "SELECT p " + baseQuery + orderBy;
        String finalCountQuery = "SELECT COUNT(p) " + baseQuery;

        TypedQuery<Product> query = entityManager.createQuery(finalDataQuery, Product.class);
        TypedQuery<Long> countQ = entityManager.createQuery(finalCountQuery, Long.class);

        params.forEach((k, v) -> {
            query.setParameter(k, v);
            countQ.setParameter(k, v);
        });

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<Product> content = query.getResultList();
        Long total = countQ.getSingleResult();

        Page<Product> productPage = new PageImpl<>(content, PageRequest.of(page, size), total);
        return productPage.map(productResponseMapper::toResponse);
    } 

}
