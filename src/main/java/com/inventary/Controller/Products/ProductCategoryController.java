package com.inventary.Controller.Products;

import com.inventary.Model.Products.ProductCategory;
import com.inventary.Services.Products.ProductCategoryService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/product-category")
public class ProductCategoryController {
    private final ProductCategoryService productCategoryService;

    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    @GetMapping("/getAllCategories")
    public ResponseEntity<Page<ProductCategory>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<ProductCategory> productCategories = productCategoryService.getAllCategories(page, size);
            return ResponseEntity.ok(productCategories);
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getCategoryById/{id}")
    public ProductCategory getCategoryById(Long id) {
        return productCategoryService.getCategoryById(id).orElse(null);
    }

    @PostMapping("/addCategory")
    public ResponseEntity<Map<String, String>> addCategory(@RequestBody ProductCategory category) {
        ProductCategory productCategory = productCategoryService.addCategory(category);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Category created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PutMapping("/update/{idCategory}")
    public ResponseEntity<ProductCategory> updateCategory(
            @PathVariable Long idCategory,
            @RequestBody ProductCategory updatedCategory) {
        ProductCategory category = productCategoryService.updateCategory(idCategory, updatedCategory);
        return ResponseEntity.ok(category);
    }

    @PatchMapping("/{idCategory}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateCategory(@PathVariable Long idCategory) {
        productCategoryService.deactivateCategory(idCategory);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Category deactivated successfully.");
        return ResponseEntity.ok(response);
    }
}
