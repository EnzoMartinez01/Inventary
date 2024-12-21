package com.inventary.Model.Products;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "dbo.product_category")
@Data
public class ProductCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategory;

    @Column(nullable = false, unique = true)
    private String categoryName;

    private String categoryDescription;

    private Boolean isActive;

}
