package com.inventary.Controller.Products;

import com.inventary.Model.Products.WareHouse;
import com.inventary.Services.Products.WareHouseService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/warehouse")
public class WareHouseController {
    private final WareHouseService wareHouseService;

    public WareHouseController(WareHouseService wareHouseService) {
        this.wareHouseService = wareHouseService;
    }

    @GetMapping("/getAllWarehouses")
    public ResponseEntity<Page<WareHouse>> getAllWarehouses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<WareHouse> warehouses = wareHouseService.getWarehouse(page, size);
            return ResponseEntity.ok(warehouses);
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getWarehouseById/{id}")
    public WareHouse getWarehouseById(Long id) {
        return wareHouseService.getWarehouseById(id);
    }

    @PostMapping("/addWarehouse")
    public ResponseEntity<Map<String, String>> addWarehouse(@RequestBody WareHouse warehouse) {
        WareHouse savedWarehouse = wareHouseService.addWarehouse(warehouse);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Warehouse created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update/{idWarehouse}")
    public ResponseEntity<WareHouse> updateWarehouse(
            @PathVariable Long idWarehouse,
            @RequestBody WareHouse updatedWarehouse) {
        WareHouse warehouse = wareHouseService.updateWarehouse(idWarehouse, updatedWarehouse);
        return ResponseEntity.ok(warehouse);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateWarehouse(@PathVariable Long id) {
        wareHouseService.deactivateWarehouse(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Warehouse deactivated successfully");
        return ResponseEntity.ok(response);
    }
}
