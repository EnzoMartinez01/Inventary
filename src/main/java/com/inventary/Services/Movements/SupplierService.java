package com.inventary.Services.Movements;

import com.inventary.Model.Movements.Supplier;
import com.inventary.Repository.Movements.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SupplierService {
    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public Page<Supplier> getAllSuppliers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return supplierRepository.findAll(pageable);
    }

    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id).orElse(null);
    }

    public Supplier addSupplier(Supplier supplier) {
        supplier.setIsActive(true);
        return supplierRepository.save(supplier);
    }

    public Supplier updateSupplier(Long idSupplier, Supplier updatedSupplier) {
        Supplier existingSupplier = supplierRepository.findById(idSupplier)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with ID: " + idSupplier));

        existingSupplier.setName(updatedSupplier.getName() != null ? updatedSupplier.getName() : existingSupplier.getName());
        existingSupplier.setEmail(updatedSupplier.getEmail() != null ? updatedSupplier.getEmail() : existingSupplier.getEmail());
        existingSupplier.setPhoneNumber(updatedSupplier.getPhoneNumber() != null ? updatedSupplier.getPhoneNumber() : existingSupplier.getPhoneNumber());
        existingSupplier.setIsActive(updatedSupplier.getIsActive() != null ? updatedSupplier.getIsActive() : existingSupplier.getIsActive());

        return supplierRepository.save(existingSupplier);
    }

    public void deactivateSupplier(Long idSupplier) {
        Supplier supplier = supplierRepository.findById(idSupplier)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with ID: " + idSupplier));

        if (!supplier.getIsActive()) {
            throw new IllegalStateException("Supplier is already deactivated.");
        }

        supplier.setIsActive(false);
        supplierRepository.save(supplier);
    }
}
