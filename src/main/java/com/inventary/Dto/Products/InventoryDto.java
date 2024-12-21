package com.inventary.Dto.Products;

import lombok.Data;

@Data
public class InventoryDto {
    private Long idInventory;
    private Long idProduct;
    private String productName;
    private String productDescription;
    private String productCode;
    private Long idCategory;
    private String categoryName;
    private Double productPrice;
    private Long idSupplier;
    private String supplierName;
    private Long idWarehouse;
    private String warehouseName;
    private Integer quantity;
    private Long idUser;
    private String username;
}
