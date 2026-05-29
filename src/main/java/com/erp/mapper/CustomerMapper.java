package com.erp.mapper;

import com.erp.dto.CreateCustomerRequest;
import com.erp.dto.CustomerResponse;
import com.erp.dto.UpdateCustomerRequest;
import com.erp.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    /**
     * Convert CreateCustomerRequest → Customer Entity
     * Used when creating new customer
     */
    public Customer toEntity(CreateCustomerRequest request) {
        if (request == null) {
            return null;
        }

        return Customer.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .city(request.getCity())
                .taxNumber(request.getTaxNumber())
                .creditLimit(request.getCreditLimit() != null ? request.getCreditLimit() : java.math.BigDecimal.ZERO)
                .branchId(request.getBranchId())
                .isActive(true)
                .isDeleted(false)
                .build();
    }

    /**
     * Convert UpdateCustomerRequest → Update existing Customer Entity
     * Used when updating customer
     */
    public void updateEntity(UpdateCustomerRequest request, Customer customer) {
        if (request == null) {
            return;
        }

        if (request.getName() != null && !request.getName().isEmpty()) {
            customer.setName(request.getName());
        }

        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            customer.setEmail(request.getEmail());
        }

        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            customer.setPhone(request.getPhone());
        }

        if (request.getAddress() != null) {
            customer.setAddress(request.getAddress());
        }

        if (request.getCity() != null) {
            customer.setCity(request.getCity());
        }

        if (request.getTaxNumber() != null) {
            customer.setTaxNumber(request.getTaxNumber());
        }

        if (request.getCreditLimit() != null) {
            customer.setCreditLimit(request.getCreditLimit());
        }

        if (request.getIsActive() != null) {
            customer.setIsActive(request.getIsActive());
        }
    }

    /**
     * Convert Customer Entity → CustomerResponse DTO
     * Used when returning customer data to client
     */
    public CustomerResponse toResponse(Customer customer) {
        if (customer == null) {
            return null;
        }

        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .city(customer.getCity())
                .taxNumber(customer.getTaxNumber())
                .creditLimit(customer.getCreditLimit())
                .branchId(customer.getBranchId())
                .isActive(customer.getIsActive())
                .createdById(customer.getCreatedById())
                .createdAt(customer.getCreatedAt())
                .updatedById(customer.getUpdatedById())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }

    /**
     * Convert List of Customers → List of Responses
     */
    public java.util.List<CustomerResponse> toResponseList(java.util.List<Customer> customers) {
        if (customers == null) {
            return null;
        }

        return customers.stream()
                .map(this::toResponse)
                .collect(java.util.stream.Collectors.toList());
    }
}