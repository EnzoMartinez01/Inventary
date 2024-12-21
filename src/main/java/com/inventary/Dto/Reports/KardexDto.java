package com.inventary.Dto.Reports;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KardexDto {
    private LocalDateTime movementDate;
    private String movementIdentifier;
    private String movementType;
    private Integer beforeStock;
    private Integer quantity;
    private Integer afterStock;
    private String productName;
    private String reason;
    private Double price;
    private String createdBy;
    private Integer currentStock;
}
