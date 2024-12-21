package com.inventary.Model.Products;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.inventary.Deserializer.Authentication.UsersDeserializer;
import com.inventary.Deserializer.Products.ProductDeserializer;
import com.inventary.Deserializer.Products.WareHouseDeserializer;
import com.inventary.Model.Authentication.Users;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "dbo.inventory")
@Data
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInventory;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonDeserialize(using = ProductDeserializer.class)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    @JsonDeserialize(using = WareHouseDeserializer.class)
    private WareHouse warehouse;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonDeserialize(using = UsersDeserializer.class)
    private Users userCreated;
}
