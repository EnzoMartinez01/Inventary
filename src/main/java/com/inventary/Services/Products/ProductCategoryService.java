package com.inventary.Services.Products;

import com.inventary.Model.Products.ProductCategory;
import com.inventary.Repository.Products.ProductCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductCategoryService {
    private final ProductCategoryRepository categoryRepository;

    public ProductCategoryService(ProductCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Page<ProductCategory> getAllCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return categoryRepository.findAll(pageable);
    }

    public Optional<ProductCategory> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public ProductCategory addCategory(ProductCategory category) {
        category.setIsActive(true);
        return categoryRepository.save(category);
    }

    public ProductCategory updateCategory(Long idCategory, ProductCategory updatedCategory) {
        ProductCategory existingCategory = categoryRepository.findById(idCategory)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + idCategory));

        existingCategory.setCategoryName(updatedCategory.getCategoryName() != null ? updatedCategory.getCategoryName() : existingCategory.getCategoryName());
        existingCategory.setCategoryDescription(updatedCategory.getCategoryDescription() != null ? updatedCategory.getCategoryDescription() : existingCategory.getCategoryDescription());
        existingCategory.setIsActive(updatedCategory.getIsActive() != null ? updatedCategory.getIsActive() : existingCategory.getIsActive());

        return categoryRepository.save(existingCategory);
    }

    public void deactivateCategory(Long idCategory) {
        ProductCategory category = categoryRepository.findById(idCategory)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + idCategory));

        if (!category.getIsActive()) {
            throw new IllegalStateException("Category is already deactivated.");
        }

        category.setIsActive(false);
        categoryRepository.save(category);
    }
}
