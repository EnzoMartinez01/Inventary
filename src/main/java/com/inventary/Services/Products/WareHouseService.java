package com.inventary.Services.Products;

import com.inventary.Model.Products.Inventory;
import com.inventary.Model.Products.WareHouse;
import com.inventary.Repository.Products.InventoryRepository;
import com.inventary.Repository.Products.WareHouseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class WareHouseService {
    private final WareHouseRepository wareHouseRepository;
    private final InventoryRepository inventoryRepository;

    public WareHouseService(WareHouseRepository wareHouseRepository,
                            InventoryRepository inventoryRepository) {
        this.wareHouseRepository = wareHouseRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public Page<WareHouse> getWarehouse(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return wareHouseRepository.findAll(pageable);
    }

    public WareHouse getWarehouseById(Long id) {
        return wareHouseRepository.findById(id).orElse(null);
    }

    public WareHouse addWarehouse(WareHouse warehouse) {
        warehouse.setIsActive(true);
        return wareHouseRepository.save(warehouse);
    }

    public WareHouse updateWarehouse(Long idWarehouse, WareHouse updatedWarehouse) {
        WareHouse existingWarehouse = wareHouseRepository.findById(idWarehouse)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with ID: " + idWarehouse));

        existingWarehouse.setCode(updatedWarehouse.getCode() != null ? updatedWarehouse.getCode() : existingWarehouse.getCode());
        existingWarehouse.setName(updatedWarehouse.getName() != null ? updatedWarehouse.getName() : existingWarehouse.getName());
        existingWarehouse.setLocation(updatedWarehouse.getLocation() != null ? updatedWarehouse.getLocation() : existingWarehouse.getLocation());
        existingWarehouse.setIsActive(updatedWarehouse.getIsActive() != null ? updatedWarehouse.getIsActive() : existingWarehouse.getIsActive());

        return wareHouseRepository.save(existingWarehouse);
    }

    public void deactivateWarehouse(Long idWarehouse) {
        WareHouse warehouse = wareHouseRepository.findById(idWarehouse)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with ID: " + idWarehouse));

        if (!warehouse.getIsActive()) {
            throw new IllegalStateException("Warehouse is already deactivated.");
        }

        warehouse.setIsActive(false);
        wareHouseRepository.save(warehouse);
    }
}
