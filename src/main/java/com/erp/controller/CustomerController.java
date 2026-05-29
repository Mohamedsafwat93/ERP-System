package com.erp.controller;

import com.erp.dto.ApiResponse;
import com.erp.dto.CreateCustomerRequest;
import com.erp.dto.CustomerResponse;
import com.erp.dto.UpdateCustomerRequest;
import com.erp.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    // ============================================
    // CREATE - POST
    // ============================================

    /**
     * Create new customer
     * POST /api/customers
     *
     * @param request CreateCustomerRequest with customer details
     * @return 201 CREATED with CustomerResponse
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request) {

        log.info("POST /customers - Creating new customer: {}", request.getName());

        try {
            // ✅ Call service to create customer
            CustomerResponse response = customerService.createCustomer(request);

            // ✅ Return 201 CREATED
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponse.<CustomerResponse>builder()
                            .success(true)
                            .message("Customer created successfully")
                            .data(response)
                            .code(201)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to create customer: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<CustomerResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }


    // ============================================
    // READ - GET
    // ============================================

    /**
     * Get single customer by ID
     * GET /api/customers/{id}
     *
     * @param id Customer ID
     * @return 200 OK with CustomerResponse
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'VIEWER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(
            @PathVariable Long id) {

        log.info("GET /customers/{} - Fetching customer", id);

        try {
            // ✅ Call service to get customer
            CustomerResponse response = customerService.getCustomerById(id);

            // ✅ Return 200 OK
            return ResponseEntity.ok(
                    ApiResponse.<CustomerResponse>builder()
                            .success(true)
                            .message("Customer retrieved successfully")
                            .data(response)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to fetch customer {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.<CustomerResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(404)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Get all customers by branch (paginated)
     * GET /api/customers?branchId=1&page=0&size=20&sort=name,asc
     *
     * @param branchId Branch ID (required query param)
     * @param pageable Pagination (page, size, sort)
     * @return 200 OK with Page of CustomerResponse
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'VIEWER')")
    public ResponseEntity<ApiResponse<Page<CustomerResponse>>> getCustomersByBranch(
            @RequestParam Long branchId,
            Pageable pageable) {

        log.info("GET /customers - Fetching customers for branch: {} with pageable: {}",
                branchId, pageable);

        try {
            // ✅ Call service with pagination
            Page<CustomerResponse> response = customerService.getCustomersByBranch(branchId, pageable);

            // ✅ Return 200 OK with paginated data
            return ResponseEntity.ok(
                    ApiResponse.<Page<CustomerResponse>>builder()
                            .success(true)
                            .message("Customers retrieved successfully")
                            .data(response)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to fetch customers: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<Page<CustomerResponse>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Search customers by name
     * GET /api/customers/search?name=ahmed&branchId=1&page=0&size=20
     *
     * @param name Search term
     * @param branchId Branch ID
     * @param pageable Pagination
     * @return 200 OK with matching customers
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'VIEWER')")
    public ResponseEntity<ApiResponse<Page<CustomerResponse>>> searchCustomers(
            @RequestParam String name,
            @RequestParam Long branchId,
            Pageable pageable) {

        log.info("GET /customers/search - Searching customers: name='{}', branchId={}",
                name, branchId);

        try {
            // ✅ Call service to search
            Page<CustomerResponse> response = customerService.searchCustomersByName(name, branchId, pageable);

            // ✅ Return 200 OK
            return ResponseEntity.ok(
                    ApiResponse.<Page<CustomerResponse>>builder()
                            .success(true)
                            .message("Search results retrieved successfully")
                            .data(response)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Search failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<Page<CustomerResponse>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Get customer by email
     * GET /api/customers/email?email=ahmed@example.com
     *
     * @param email Customer email
     * @return 200 OK with CustomerResponse
     */
    @GetMapping("/email")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'VIEWER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerByEmail(
            @RequestParam String email) {

        log.info("GET /customers/email - Fetching customer by email: {}", email);

        try {
            // ✅ Call service
            CustomerResponse response = customerService.getCustomerByEmail(email);

            // ✅ Return 200 OK
            return ResponseEntity.ok(
                    ApiResponse.<CustomerResponse>builder()
                            .success(true)
                            .message("Customer retrieved successfully")
                            .data(response)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to fetch customer by email: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.<CustomerResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(404)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }


    // ============================================
    // UPDATE - PUT
    // ============================================

    /**
     * Update existing customer
     * PUT /api/customers/{id}
     *
     * @param id Customer ID
     * @param request UpdateCustomerRequest (partial update allowed)
     * @return 200 OK with updated CustomerResponse
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCustomerRequest request) {

        log.info("PUT /customers/{} - Updating customer", id);

        try {
            // ✅ Call service to update
            CustomerResponse response = customerService.updateCustomer(id, request);

            // ✅ Return 200 OK
            return ResponseEntity.ok(
                    ApiResponse.<CustomerResponse>builder()
                            .success(true)
                            .message("Customer updated successfully")
                            .data(response)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to update customer {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ApiResponse.<CustomerResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }


    // ============================================
    // DELETE
    // ============================================

    /**
     * Delete (soft delete) customer
     * DELETE /api/customers/{id}
     *
     * @param id Customer ID to delete
     * @return 204 NO CONTENT
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(
            @PathVariable Long id) {

        log.info("DELETE /customers/{} - Deleting customer", id);

        try {
            // ✅ Call service to delete
            customerService.deleteCustomer(id);

            // ✅ Return 204 NO CONTENT
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            log.error("Failed to delete customer {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.<Void>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(404)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }
}