package com.inventary.Dto.Movements;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InventoryMovementDto {
    private Long idInventoryMovement;
    private Long idWarehouse;
    private String warehouseName;
    private Long idProduct;
    private String productName;
    private Integer beforeStock;
    private Integer quantity;
    private Integer afterStock;
    private String movementType;
    private Double price;
    private String reason;
    private LocalDateTime movementDate;
    private Long idUser;
    private String userName;
}
