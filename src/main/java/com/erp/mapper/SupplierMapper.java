package com.erp.mapper;

import com.erp.dto.CreateSupplierRequest;
import com.erp.dto.UpdateSupplierRequest;
import com.erp.dto.SupplierResponse;
import com.erp.entity.Supplier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SupplierMapper {

    public Supplier toEntity(CreateSupplierRequest request) {
        return Supplier.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .taxNumber(request.getTaxNumber())
                .paymentTerms(request.getPaymentTerms())
                .branchId(request.getBranchId())
                .isActive(true)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public SupplierResponse toResponse(Supplier supplier) {
        return SupplierResponse.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .address(supplier.getAddress())
                .city(supplier.getCity())
                .taxNumber(supplier.getTaxNumber())          // ← ADDED
                .paymentTerms(supplier.getPaymentTerms())
                .creditLimit(supplier.getCreditLimit())
                .rating(supplier.getRating())
                .branchId(supplier.getBranchId())            // ← ADDED
                .isActive(supplier.getIsActive())
                .createdAt(supplier.getCreatedAt())
                .updatedAt(supplier.getUpdatedAt())
                .build();
    }

    public void updateEntityFromRequest(UpdateSupplierRequest request, Supplier supplier) {
        if (request.getName() != null) {
            supplier.setName(request.getName());
        }
        if (request.getEmail() != null) {
            supplier.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            supplier.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            supplier.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            supplier.setCity(request.getCity());
        }
        if (request.getTaxNumber() != null) {
            supplier.setTaxNumber(request.getTaxNumber());
        }
        if (request.getPaymentTerms() != null) {
            supplier.setPaymentTerms(request.getPaymentTerms());
        }
        if (request.getIsActive() != null) {
            supplier.setIsActive(request.getIsActive());
        }
        supplier.setUpdatedAt(LocalDateTime.now());
    }
}