package com.inventary.Services.Movements;

import com.inventary.Dto.Movements.InventoryMovementDto;
import com.inventary.Model.Authentication.Security.CustomUserDetails;
import com.inventary.Model.Movements.InventoryMovement;
import com.inventary.Model.Movements.MovementType;
import com.inventary.Model.Products.Inventory;
import com.inventary.Model.Products.Product;
import com.inventary.Model.Products.WareHouse;
import com.inventary.Repository.Movements.InventoryMovementRepository;
import com.inventary.Repository.Products.InventoryRepository;
import com.inventary.Repository.Products.ProductRepository;
import com.inventary.Repository.Products.WareHouseRepository;
import com.inventary.Services.Reports.AuditService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryMovementService {
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final WareHouseRepository wareHouseRepository;
    private final ProductRepository productRepository;
    private final AuditService auditService;

    public InventoryMovementService(InventoryRepository inventoryRepository,
                                    InventoryMovementRepository inventoryMovementRepository,
                                    WareHouseRepository wareHouseRepository,
                                    ProductRepository productRepository,
                                    AuditService auditService) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryMovementRepository = inventoryMovementRepository;
        this.wareHouseRepository = wareHouseRepository;
        this.productRepository = productRepository;
        this.auditService = auditService;
    }

    public InventoryMovement registerMovement(Long warehouseId, Long productId, Integer quantity, MovementType type, String reason) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomUserDetails)) {
            throw new IllegalStateException("Usuario no autenticado o tipo incorrecto");
        }

        CustomUserDetails userDetails = (CustomUserDetails) principal;

        WareHouse warehouse = wareHouseRepository.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el almacén con ID: " + warehouseId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró el producto con ID: " + productId));

        Inventory inventory = inventoryRepository.findByWarehouseAndProduct(warehouse, product)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró inventario para el producto en este almacén"));

        if (type == MovementType.EXIT && inventory.getQuantity() < quantity) {
            throw new IllegalStateException("Stock insuficiente para realizar la salida del producto");
        }

        int beforeStock = inventory.getQuantity();
        int afterStock = type == MovementType.ENTRY ? beforeStock + quantity : beforeStock - quantity;
        inventory.setQuantity(afterStock);
        inventoryRepository.save(inventory);

        String movementIdentifier = generateMovementIdentifier(product);

        InventoryMovement movement = new InventoryMovement();
        movement.setMovementIdentifier(movementIdentifier);
        movement.setWarehouse(warehouse);
        movement.setProduct(product);
        movement.setType(type);
        movement.setQuantity(quantity);
        movement.setBeforeStock(beforeStock);
        movement.setAfterStock(afterStock);
        movement.setReason(reason != null ? reason : "Sin razón especificada");
        movement.setMovementDate(LocalDateTime.now());
        movement.setPrice(product.getPrice() != null ? product.getPrice() : 0.0);
        movement.setCreatedBy(userDetails.getUser());

        InventoryMovement savedMovement = inventoryMovementRepository.save(movement);

        String action = type == MovementType.ENTRY ? "Entrada de inventario" : "Salida de inventario";
        String details = String.format(
                "Almacén: %s, Producto: %s, Cantidad: %d, Razón: %s, Stock Final: %d",
                warehouse.getName(), product.getProductName(), quantity, reason, afterStock
        );
        auditService.logAction(action, userDetails.getUsername(), details);

        return savedMovement;
    }

    private String generateMovementIdentifier(Product product) {
        List<InventoryMovement> movements = inventoryMovementRepository.findByProductOrderByMovementDateAsc(product);
        int nextNumber = movements.size() + 1;

        return String.format("%s%03d", product.getProductName(), nextNumber);
    }


    public Page<InventoryMovementDto> getAllMovements(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return inventoryMovementRepository.findAll(pageable).map(this::mapToDto);
    }

    public InventoryMovementDto mapToDto(InventoryMovement inventoryMovement) {
        InventoryMovementDto dto = new InventoryMovementDto();
        dto.setIdInventoryMovement(inventoryMovement.getIdMovement());
        dto.setIdWarehouse(inventoryMovement.getWarehouse().getIdWarehouse());
        dto.setWarehouseName(inventoryMovement.getWarehouse().getName());
        dto.setIdProduct(inventoryMovement.getProduct().getIdProduct());
        dto.setProductName(inventoryMovement.getProduct().getProductName());
        dto.setQuantity(inventoryMovement.getQuantity());
        dto.setMovementType(inventoryMovement.getType().name());
        dto.setReason(inventoryMovement.getReason());
        dto.setMovementDate(inventoryMovement.getMovementDate());
        dto.setIdUser(inventoryMovement.getCreatedBy().getIdUser());
        dto.setUserName(inventoryMovement.getCreatedBy().getUsername());
        return dto;
    }
}
