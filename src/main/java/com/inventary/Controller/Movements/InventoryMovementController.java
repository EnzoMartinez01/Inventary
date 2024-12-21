package com.inventary.Controller.Movements;

import com.inventary.Model.Movements.InventoryMovement;
import com.inventary.Model.Movements.MovementType;
import com.inventary.Services.Movements.InventoryMovementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/inventory-movement")
public class InventoryMovementController {
    private final InventoryMovementService inventoryMovementService;

    public InventoryMovementController(InventoryMovementService inventoryMovementService) {
        this.inventoryMovementService = inventoryMovementService;
    }

    @PostMapping("/registerMovement")
    public ResponseEntity<Map<String, String>> registerMovement(
            @RequestParam Long warehouseId,
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            @RequestParam("movementType") MovementType type,
            @RequestParam String reason
    ) {
        InventoryMovement movement = inventoryMovementService.registerMovement(warehouseId, productId, quantity, type, reason);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Movement registered successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
