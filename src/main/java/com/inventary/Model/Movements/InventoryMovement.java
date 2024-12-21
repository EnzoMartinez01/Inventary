package com.inventary.Model.Movements;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.inventary.Deserializer.Authentication.UsersDeserializer;
import com.inventary.Deserializer.Products.ProductDeserializer;
import com.inventary.Deserializer.Products.WareHouseDeserializer;
import com.inventary.Model.Authentication.Users;
import com.inventary.Model.Products.Product;
import com.inventary.Model.Products.WareHouse;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "dbo.inventory_movement")
@Data
public class InventoryMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMovement;

    private String movementIdentifier;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonDeserialize(using = ProductDeserializer.class)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    @JsonDeserialize(using = WareHouseDeserializer.class)
    private WareHouse warehouse;

    private Integer beforeStock;

    private Integer afterStock;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MovementType type;

    private String reason;

    private Double price;

    private LocalDateTime movementDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonDeserialize(using = UsersDeserializer.class)
    private Users createdBy;
}
