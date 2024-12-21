package com.inventary.Controller.Products;

import com.inventary.Dto.Products.InventoryDto;
import com.inventary.Model.Products.Inventory;
import com.inventary.Services.Products.InventoryService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{warehouseId}")
    public ResponseEntity<Page<Inventory>> getWarehouseInventory(
            @PathVariable Long warehouseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Inventory> inventoryPage = inventoryService.getWarehouseInventory(warehouseId, page, size);
        return ResponseEntity.ok(inventoryPage);
    }

    @PostMapping("/addProductToInventory")
    public ResponseEntity<Map<String, String>> addProductToInventory(@RequestParam Long warehouseId,
                                                                     @RequestParam Long productId,
                                                                     @RequestParam Integer quantity) {
        Inventory inventory = inventoryService.addProductToInventory(warehouseId, productId, quantity);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Inventory created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/getInventory")
    public ResponseEntity<Page<InventoryDto>> getUserInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<InventoryDto> inventoryPage = inventoryService.getUserInventory(page, size);
        return ResponseEntity.ok(inventoryPage);
    }
}
