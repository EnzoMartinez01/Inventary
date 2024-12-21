package com.inventary.Services.Products;

import com.inventary.Dto.Products.ProductDto;
import com.inventary.Model.Products.Product;
import com.inventary.Repository.Products.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<ProductDto> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable).map(this::mapToDto);
    }

    public Page<ProductDto> getFilteredProducts(Long categoryId, Long supplierId, Double minPrice, Double maxPrice, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByFilters(categoryId, supplierId, minPrice, maxPrice, pageable).map(this::mapToDto);
    }

    public ProductDto mapToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setIdProduct(product.getIdProduct());
        dto.setProductName(product.getProductName());
        dto.setProductDescription(product.getDescription());
        dto.setProductCode(product.getProductCode());
        dto.setIdCategory(product.getProductCategory().getIdCategory());
        dto.setCategoryName(product.getProductCategory().getCategoryName());
        dto.setProductPrice(product.getPrice());
        dto.setIdSupplier(product.getSupplier().getIdSupplier());
        dto.setSupplierName(product.getSupplier().getName());
        dto.setProductStock(product.getProductStock());
        dto.setIsActive(product.getIsActive());
        return dto;
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product addProduct(Product product) {
        product.setIsActive(true);
        return productRepository.save(product);
    }

    public Product updateProduct(Long idProduct, Product updatedProduct) {
        Product existingProduct = productRepository.findById(idProduct)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + idProduct));

        if (updatedProduct.getProductCode() != null) {
            existingProduct.setProductCode(updatedProduct.getProductCode());
        }
        if (updatedProduct.getProductName() != null) {
            existingProduct.setProductName(updatedProduct.getProductName());
        }
        if (updatedProduct.getDescription() != null) {
            existingProduct.setDescription(updatedProduct.getDescription());
        }
        if (updatedProduct.getPrice() != null) {
            existingProduct.setPrice(updatedProduct.getPrice());
        }
        if (updatedProduct.getProductStock() != null) {
            existingProduct.setProductStock(updatedProduct.getProductStock());
        }
        if (updatedProduct.getProductCategory() != null) {
            existingProduct.setProductCategory(updatedProduct.getProductCategory());
        }
        if (updatedProduct.getSupplier() != null) {
            existingProduct.setSupplier(updatedProduct.getSupplier());
        }
        if (updatedProduct.getIsActive() != null) {
            existingProduct.setIsActive(updatedProduct.getIsActive());
        }

        return productRepository.save(existingProduct);
    }


    public void deactivateProduct(Long idProduct) {
        Product product = productRepository.findById(idProduct)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + idProduct));

        if (!product.getIsActive()) {
            throw new IllegalStateException("Product is already deactivated.");
        }

        product.setIsActive(false);
        productRepository.save(product);
    }
}
