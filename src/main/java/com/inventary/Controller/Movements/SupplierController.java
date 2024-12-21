package com.inventary.Controller.Movements;

import com.inventary.Model.Movements.Supplier;
import com.inventary.Services.Movements.SupplierService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/supplier")
public class SupplierController {
    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping("/getAllSuppliers")
    public ResponseEntity<Page<Supplier>> getAllSuppliers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Supplier> suppliers = supplierService.getAllSuppliers(page, size);
            return ResponseEntity.ok(suppliers);
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getSupplierById/{id}")
    public Supplier getSupplierById(Long id) {
        return supplierService.getSupplierById(id);
    }

    @PostMapping("/addSupplier")
    public ResponseEntity<Map<String, String>> addSupplier(@RequestBody Supplier supplier) {
        Supplier savedSupplier = supplierService.addSupplier(supplier);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Supplier created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update/{idSupplier}")
    public ResponseEntity<Supplier> updateSupplier(
            @PathVariable Long idSupplier,
            @RequestBody Supplier updatedSupplier) {
        Supplier supplier = supplierService.updateSupplier(idSupplier, updatedSupplier);
        return ResponseEntity.ok(supplier);
    }

    @PatchMapping("/{idSupplier}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateSupplier(@PathVariable Long idSupplier) {
        supplierService.deactivateSupplier(idSupplier);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Supplier deactivated successfully.");
        return ResponseEntity.ok(response);
    }
}
