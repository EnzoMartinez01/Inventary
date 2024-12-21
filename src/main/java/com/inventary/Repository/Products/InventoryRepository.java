package com.inventary.Repository.Products;

import com.inventary.Model.Authentication.Users;
import com.inventary.Model.Products.Inventory;
import com.inventary.Model.Products.Product;
import com.inventary.Model.Products.WareHouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Page<Inventory> findByWarehouse(WareHouse warehouse, Pageable pageable);
    Optional<Inventory> findByWarehouseAndProduct(WareHouse warehouse, Product product);
    Page<Inventory> findByUserCreated(Users userCreated, Pageable pageable);
    List<Inventory> findByWarehouse_IdWarehouse(Long warehouse);
}
