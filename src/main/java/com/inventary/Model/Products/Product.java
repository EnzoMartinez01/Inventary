package com.inventary.Model.Products;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.inventary.Deserializer.Movements.SupplierDeserializer;
import com.inventary.Deserializer.Products.ProductCategoryDeserializer;
import com.inventary.Model.Movements.Supplier;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "dbo.products")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long idProduct;

    @Column(unique = true, nullable = false)
    private String productCode;

    @Column(nullable = false)
    private String productName;
    private String description;

    @ManyToOne
    @JsonDeserialize(using = ProductCategoryDeserializer.class)
    @JoinColumn(name = "product_category_id")
    private ProductCategory productCategory;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer productStock;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    @JsonDeserialize(using = SupplierDeserializer.class)
    private Supplier supplier;

    private Boolean isActive;

}
