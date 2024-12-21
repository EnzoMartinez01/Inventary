package com.inventary.Model.Movements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.inventary.Model.Products.Product;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "dbo.supplier")
@Data
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSupplier;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(unique = true)
    private String email;

    private String phoneNumber;

    private Boolean isActive;

    @OneToMany(mappedBy = "supplier")
    @JsonIgnore
    private List<Product> products;
}
