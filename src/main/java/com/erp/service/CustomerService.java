package com.erp.service;

import com.erp.dto.CreateCustomerRequest;
import com.erp.dto.CustomerResponse;
import com.erp.dto.UpdateCustomerRequest;
import com.erp.entity.Customer;
import com.erp.exception.DuplicateResourceException;
import com.erp.exception.ResourceNotFoundException;
import com.erp.mapper.CustomerMapper;
import com.erp.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    // ============================================
    // CREATE
    // ============================================

    /**
     * Create new customer
     * @param request CreateCustomerRequest
     * @return CustomerResponse with created customer details
     */
    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        log.info("Creating new customer: {}", request.getName());

        // ✅ VALIDATION: Check if email already exists
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            boolean emailExists = customerRepository.existsByEmailAndNotDeleted(request.getEmail());
            if (emailExists) {
                log.warn("Customer creation failed: Email already exists - {}", request.getEmail());
                throw new DuplicateResourceException("Email already registered: " + request.getEmail());
            }
        }

        // ✅ CONVERT: Request DTO → Entity
        Customer customer = customerMapper.toEntity(request);

        // ✅ SET AUDIT: Who created this customer
        Long currentUserId = getCurrentUserId();
        customer.setCreatedById(currentUserId);
        customer.setUpdatedById(currentUserId);

        // ✅ SAVE: To database
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created successfully with ID: {}", savedCustomer.getId());

        // ✅ CONVERT: Entity → Response DTO
        return customerMapper.toResponse(savedCustomer);
    }


    // ============================================
    // READ
    // ============================================

    /**
     * Get single customer by ID
     * @param id Customer ID
     * @return CustomerResponse
     */
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        log.info("Fetching customer with ID: {}", id);

        Customer customer = customerRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> {
                    log.error("Customer not found: {}", id);
                    return new ResourceNotFoundException("Customer not found with ID: " + id);
                });

        return customerMapper.toResponse(customer);
    }

    /**
     * Get all customers by branch (paginated)
     * @param branchId Branch ID
     * @param pageable Pagination info (page, size, sort)
     * @return Page of CustomerResponse
     */
    @Transactional(readOnly = true)
    public Page<CustomerResponse> getCustomersByBranch(Long branchId, Pageable pageable) {
        log.info("Fetching customers for branch: {} with pagination: page={}, size={}",
                branchId, pageable.getPageNumber(), pageable.getPageSize());

        // ✅ Get paginated customers from repository
        Page<Customer> customers = customerRepository.findByBranchIdAndNotDeleted(branchId, pageable);

        // ✅ Convert list of entities to DTOs
        List<CustomerResponse> responses = customerMapper.toResponseList(customers.getContent());

        // ✅ Return as Page (maintains pagination info)
        return new PageImpl<>(responses, pageable, customers.getTotalElements());
    }

    /**
     * Search customers by name
     * @param name Search term
     * @param branchId Branch ID
     * @param pageable Pagination
     * @return Page of matching customers
     */
    @Transactional(readOnly = true)
    public Page<CustomerResponse> searchCustomersByName(String name, Long branchId, Pageable pageable) {
        log.info("Searching customers by name: '{}' in branch: {}", name, branchId);

        // ✅ Validation
        if (name == null || name.trim().isEmpty()) {
            log.warn("Search name is empty");
            throw new IllegalArgumentException("Search name cannot be empty");
        }

        // ✅ Repository query
        Page<Customer> customers = customerRepository.searchByNameAndBranch(name, branchId, pageable);

        // ✅ Convert and return
        List<CustomerResponse> responses = customerMapper.toResponseList(customers.getContent());
        return new PageImpl<>(responses, pageable, customers.getTotalElements());
    }

    /**
     * Get customer by email
     * @param email Customer email
     * @return CustomerResponse
     */
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerByEmail(String email) {
        log.info("Fetching customer by email: {}", email);

        Customer customer = customerRepository.findByEmailAndNotDeleted(email)
                .orElseThrow(() -> {
                    log.error("Customer not found with email: {}", email);
                    return new ResourceNotFoundException("Customer not found with email: " + email);
                });

        return customerMapper.toResponse(customer);
    }


    // ============================================
    // UPDATE
    // ============================================

    /**
     * Update existing customer
     * @param id Customer ID to update
     * @param request UpdateCustomerRequest (partial update allowed)
     * @return Updated CustomerResponse
     */
    @Transactional
    public CustomerResponse updateCustomer(Long id, UpdateCustomerRequest request) {
        log.info("Updating customer with ID: {}", id);

        // ✅ GET: Find customer
        Customer customer = customerRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> {
                    log.error("Customer not found for update: {}", id);
                    return new ResourceNotFoundException("Customer not found with ID: " + id);
                });

        // ✅ VALIDATION: Check if new email already exists (if email is being updated)
        if (request.getEmail() != null && !request.getEmail().isEmpty()
                && !request.getEmail().equals(customer.getEmail())) {

            boolean emailExists = customerRepository.existsByEmailAndNotDeleted(request.getEmail());
            if (emailExists) {
                log.warn("Update failed: Email already exists - {}", request.getEmail());
                throw new DuplicateResourceException("Email already registered: " + request.getEmail());
            }
        }

        // ✅ UPDATE: Apply changes (only non-null fields)
        customerMapper.updateEntity(request, customer);

        // ✅ SET AUDIT: Who updated this customer
        customer.setUpdatedById(getCurrentUserId());

        // ✅ SAVE: Updated entity
        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Customer updated successfully: {}", id);

        // ✅ CONVERT: Entity → Response
        return customerMapper.toResponse(updatedCustomer);
    }


    // ============================================
    // DELETE (Soft Delete)
    // ============================================

    /**
     * Soft delete customer (mark as deleted, don't remove from DB)
     * @param id Customer ID to delete
     */
    @Transactional
    public void deleteCustomer(Long id) {
        log.info("Deleting customer with ID: {}", id);

        // ✅ GET: Find customer
        Customer customer = customerRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> {
                    log.error("Customer not found for deletion: {}", id);
                    return new ResourceNotFoundException("Customer not found with ID: " + id);
                });

        // ✅ SOFT DELETE: Mark as deleted (don't actually delete)
        customer.setIsDeleted(true);
        customer.setUpdatedById(getCurrentUserId());

        // ✅ SAVE: Mark deleted in database
        customerRepository.save(customer);
        log.info("Customer soft-deleted successfully: {}", id);
    }


    // ============================================
    // HELPER METHODS
    // ============================================

    /**
     * Get current authenticated user ID
     * @return User ID from security context
     */
    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                // Extract user ID from principal
                // This assumes your User has getId() method
                Object principal = authentication.getPrincipal();
                if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                    // Return a default ID for now (you'll customize this later)
                    return 1L;
                }
            }
        } catch (Exception e) {
            log.warn("Could not get current user ID", e);
        }
        return 1L; // Default fallback
    }

    /**
     * Check if customer exists
     * @param id Customer ID
     * @return true if exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean customerExists(Long id) {
        return customerRepository.findByIdAndNotDeleted(id).isPresent();
    }

    /**
     * Get total customer count for branch
     * @param branchId Branch ID
     * @return Count of active customers
     */
    @Transactional(readOnly = true)
    public long getCustomerCountByBranch(Long branchId) {
        log.info("Getting customer count for branch: {}", branchId);
        return customerRepository.countByBranch(branchId);
    }
}