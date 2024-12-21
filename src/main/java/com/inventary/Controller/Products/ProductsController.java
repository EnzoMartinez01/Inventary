package com.inventary.Controller.Products;

import com.inventary.Dto.Products.ProductDto;
import com.inventary.Model.Products.Product;
import com.inventary.Services.Products.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
public class ProductsController {
    private final ProductService productService;

    public ProductsController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<ProductDto> products = productService.getAllProducts(page, size);
            return ResponseEntity.ok(products);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/filterProducts")
    public ResponseEntity<Page<ProductDto>> getFilteredProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<ProductDto> products = productService.getFilteredProducts(categoryId, supplierId, minPrice, maxPrice, page, size);
            return ResponseEntity.ok(products);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getProductById/{id}")
    public Product getProductById(Long id) {
        return productService.getProductById(id);
    }

    @PostMapping("/addProduct")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        if (product.getPrice() != null) {
            product.setPrice(Double.valueOf(product.getPrice().toString()));
        }

        if (product.getPrice() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        Product savedProduct = productService.addProduct(product);
        return ResponseEntity.ok(savedProduct);
    }

    @PutMapping("/update/{idProduct}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long idProduct,
            @RequestBody Product updatedProduct) {
        Product product = productService.updateProduct(idProduct, updatedProduct);
        return ResponseEntity.ok(product);
    }

    @PatchMapping("/{idProduct}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateProduct(@PathVariable Long idProduct) {
        productService.deactivateProduct(idProduct);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Product deactivated successfully.");
        return ResponseEntity.ok(response);
    }
}
