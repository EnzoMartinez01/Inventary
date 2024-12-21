package com.inventary.Dto.Products;

import lombok.Data;

@Data
public class ProductDto {
    private Long idProduct;
    private String productName;
    private String productDescription;
    private String productCode;
    private Long idCategory;
    private String categoryName;
    private Double productPrice;
    private Long idSupplier;
    private String supplierName;
    private Integer productStock;
    private Boolean isActive;
}
