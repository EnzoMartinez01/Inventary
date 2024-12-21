package com.inventary.Repository.Products;

import com.inventary.Model.Products.WareHouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface WareHouseRepository extends JpaRepository<WareHouse, Long> {
}
