package com.erp.service;

import com.erp.dto.CreateSupplierRequest;
import com.erp.dto.UpdateSupplierRequest;
import com.erp.dto.SupplierResponse;
import com.erp.entity.Supplier;
import com.erp.exception.DuplicateResourceException;
import com.erp.exception.ResourceNotFoundException;
import com.erp.mapper.SupplierMapper;
import com.erp.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    // CREATE
    public SupplierResponse createSupplier(CreateSupplierRequest request, Long branchId) {
        // Check if email already exists
        if (supplierRepository.existsByEmailAndIsDeletedFalse(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        Supplier supplier = supplierMapper.toEntity(request);
        supplier.setBranchId(branchId);
        Supplier savedSupplier = supplierRepository.save(supplier);

        return supplierMapper.toResponse(savedSupplier);
    }

    // GET ONE
    public SupplierResponse getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
        return supplierMapper.toResponse(supplier);
    }

    // GET ALL (with pagination)
    public Page<SupplierResponse> getAllSuppliers(Long branchId, Pageable pageable) {
        return supplierRepository.findByBranchIdAndIsDeletedFalse(branchId, pageable)
                .map(supplierMapper::toResponse);
    }

    // SEARCH
    public List<SupplierResponse> searchSuppliers(String query, Long branchId) {
        return supplierRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(query)
                .stream()
                .filter(s -> s.getBranchId().equals(branchId))
                .map(supplierMapper::toResponse)
                .collect(Collectors.toList());
    }

    // UPDATE
    public SupplierResponse updateSupplier(Long id, UpdateSupplierRequest request) {
        Supplier supplier = supplierRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

        // Check if email changed and already exists
        if (request.getEmail() != null && !request.getEmail().equals(supplier.getEmail())) {
            if (supplierRepository.existsByEmailAndIsDeletedFalse(request.getEmail())) {
                throw new DuplicateResourceException("Email already exists");
            }
        }

        supplierMapper.updateEntityFromRequest(request, supplier);
        Supplier updatedSupplier = supplierRepository.save(supplier);

        return supplierMapper.toResponse(updatedSupplier);
    }

    // DELETE (Soft Delete)
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));

        supplier.setIsDeleted(true);
        supplierRepository.save(supplier);
    }

    // GET ACTIVE SUPPLIERS (helper)
    public List<SupplierResponse> getActiveSuppliers(Long branchId) {
        return supplierRepository.findByBranchIdAndIsActiveTrueAndIsDeletedFalse(branchId)
                .stream()
                .map(supplierMapper::toResponse)
                .collect(Collectors.toList());
    }
}