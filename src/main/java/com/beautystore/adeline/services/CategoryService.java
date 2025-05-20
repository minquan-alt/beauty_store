package com.beautystore.adeline.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beautystore.adeline.dto.request.CategoryCreateRequest;
import com.beautystore.adeline.dto.response.CategoryResponse;
import com.beautystore.adeline.entity.Category;
import com.beautystore.adeline.exception.AppException;
import com.beautystore.adeline.exception.ErrorCode;
import com.beautystore.adeline.mapper.CategoryMapper;
import com.beautystore.adeline.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private static Logger logger = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private final CategoryMapper categoryMapper;

    public Category createCategory(CategoryCreateRequest request){
        if(categoryRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        Category category = categoryMapper.toCategory(request);
        return categoryRepository.save(category);
    }

    public List<Category> getCategories(){
        List<Category> categories = categoryRepository.findAll();
        if(categories.isEmpty()){
            throw new AppException(ErrorCode.CATEGORY_LIST_EMPTY);
        }
        return categories;
    }

    public CategoryResponse getCategory(Long id){
        logger.info("Fetching category with id: {}", id); 
        Category category = categoryRepository.findById(id)
            .orElseThrow(()  -> {
                logger.error("Category not found with id: {}", id); // 👈 Log lỗi nếu không tìm thấy user
                return new AppException(ErrorCode.CATEGORY_NOT_FOUND);
            });
        logger.info("Category found: {}", category); // 

        logger.info("Category found: {}", category);
        return categoryMapper.toCategoryResponse(category);
    }

    public CategoryResponse updateCategory(CategoryCreateRequest request, Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        if (categoryRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
        categoryMapper.updateCategory(category, request);
        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }
    

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        
        if (!category.getProducts().isEmpty()) {
            throw new AppException(ErrorCode.CATEGORY_HAS_PRODUCTS);
        }
    
        categoryRepository.delete(category);
    }
}
