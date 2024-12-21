package com.inventary.Repository.Movements;

import com.inventary.Model.Movements.InventoryMovement;
import com.inventary.Model.Products.Product;
import com.inventary.Model.Products.WareHouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
    @Query("SELECT im FROM InventoryMovement im " +
            "WHERE (:warehouseId IS NULL OR im.warehouse.idWarehouse = :warehouseId) " +
            "AND (:startDate IS NULL OR im.movementDate >= :startDate) " +
            "AND (:endDate IS NULL OR im.movementDate <= :endDate) " +
            "AND (:userId IS NULL OR im.createdBy.idUser = :userId)")
    Page<InventoryMovement> findByFilters(@Param("warehouseId") Long warehouseId,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate,
                                          @Param("userId") Long userId,
                                          Pageable pageable);

    List<InventoryMovement> findByWarehouseOrderByMovementDateAsc(WareHouse warehouseId);
    List<InventoryMovement> findByProductOrderByMovementDateAsc(Product product);
    List<InventoryMovement> findByWarehouseAndMovementDateBetweenOrderByMovementDateAsc(
            WareHouse warehouse, LocalDateTime startDate, LocalDateTime endDate
    );
}
