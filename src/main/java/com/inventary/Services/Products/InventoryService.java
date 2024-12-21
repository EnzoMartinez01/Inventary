package com.inventary.Services.Products;

import com.inventary.Dto.Products.InventoryDto;
import com.inventary.Model.Authentication.Security.CustomUserDetails;
import com.inventary.Model.Authentication.Users;
import com.inventary.Model.Products.Inventory;
import com.inventary.Model.Products.Product;
import com.inventary.Model.Products.WareHouse;
import com.inventary.Repository.Authentication.UsersRepository;
import com.inventary.Repository.Products.InventoryRepository;
import com.inventary.Repository.Products.ProductRepository;
import com.inventary.Repository.Products.WareHouseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final WareHouseRepository wareHouseRepository;
    private final ProductRepository productRepository;
    private final UsersRepository usersRepository;

    public InventoryService(InventoryRepository inventoryRepository,
                            WareHouseRepository wareHouseRepository,
                            ProductRepository productRepository,
                            UsersRepository usersRepository) {
        this.inventoryRepository = inventoryRepository;
        this.wareHouseRepository = wareHouseRepository;
        this.productRepository = productRepository;
        this.usersRepository = usersRepository;
    }

    public Page<Inventory> getWarehouseInventory(Long id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        WareHouse wareHouse = wareHouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        return inventoryRepository.findByWarehouse(wareHouse, pageable);
    }

    public Page<InventoryDto> getUserInventory(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Users currentUser = customUserDetails.getUser();

        Users user = usersRepository.findById(currentUser.getIdUser())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return inventoryRepository.findByUserCreated(user, pageable).map(this::mapToDto);
    }


    public InventoryDto mapToDto(Inventory inventory) {
        InventoryDto dto = new InventoryDto();
        dto.setIdInventory(inventory.getIdInventory());
        dto.setIdProduct(inventory.getProduct().getIdProduct());
        dto.setProductName(inventory.getProduct().getProductName());
        dto.setProductDescription(inventory.getProduct().getDescription());
        dto.setProductCode(inventory.getProduct().getProductCode());
        dto.setIdCategory(inventory.getProduct().getProductCategory().getIdCategory());
        dto.setCategoryName(inventory.getProduct().getProductCategory().getCategoryName());
        dto.setProductPrice(inventory.getProduct().getPrice());
        dto.setIdSupplier(inventory.getProduct().getSupplier().getIdSupplier());
        dto.setSupplierName(inventory.getProduct().getSupplier().getName());
        dto.setIdWarehouse(inventory.getWarehouse().getIdWarehouse());
        dto.setWarehouseName(inventory.getWarehouse().getName());
        dto.setQuantity(inventory.getQuantity());
        dto.setIdUser(inventory.getUserCreated().getIdUser());
        dto.setUsername(inventory.getUserCreated().getUsername());
        return dto;
    }

    public Inventory addProductToInventory(Long warehouseId, Long productId, Integer quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Users currentUser = customUserDetails.getUser();

        Users user = usersRepository.findById(currentUser.getIdUser())
                .orElseThrow(() -> new RuntimeException("User not found"));
        WareHouse wareHouse = wareHouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<Inventory> existingInventory = inventoryRepository.findByWarehouseAndProduct(wareHouse, product);

        Inventory inventory;
        if (existingInventory.isPresent()) {
            inventory = existingInventory.get();
            inventory.setQuantity(inventory.getQuantity() + quantity);
        } else {
            inventory = new Inventory();
            inventory.setUserCreated(user);
            inventory.setWarehouse(wareHouse);
            inventory.setProduct(product);
            inventory.setQuantity(quantity);
        }

        return inventoryRepository.save(inventory);
    }
}
