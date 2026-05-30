package com.erp.controller;

import com.erp.dto.*;
import com.erp.service.SalesOrderService;
import com.erp.util.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.erp.entity.User;

@RestController
@RequestMapping("/api/sales-orders")
@RequiredArgsConstructor
@Slf4j
public class SalesOrderController {

    private final SalesOrderService salesOrderService;
    private final MessageService messageService;

    /**
     * Create new sales order
     * POST /api/sales-orders
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SalesOrderDTO>> createSalesOrder(
            @Valid @RequestBody CreateSalesOrderRequest request,
            Authentication authentication) {

        log.info("POST /api/sales-orders - Creating sales order");

        try {
            Long userId = getUserIdFromAuthentication(authentication);
            SalesOrderDTO created = salesOrderService.createSalesOrder(request, userId);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponse.<SalesOrderDTO>builder()
                            .success(true)
                            .message(messageService.get("sales_order.created.success"))
                            .data(created)
                            .code(201)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to create sales order: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<SalesOrderDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Get all sales orders (paginated)
     * GET /api/sales-orders?page=0&size=20&sortBy=orderDate&sortDir=desc
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<SalesOrderDTO>>> getAllSalesOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("GET /api/sales-orders - Fetching all sales orders");

        try {
            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<SalesOrderDTO> orders = salesOrderService.getAllSalesOrders(pageable);

            return ResponseEntity.ok(
                    ApiResponse.<Page<SalesOrderDTO>>builder()
                            .success(true)
                            .message(messageService.get("sales_order.retrieved.success"))
                            .data(orders)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to fetch sales orders: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<Page<SalesOrderDTO>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Get sales order by ID
     * GET /api/sales-orders/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SalesOrderDTO>> getSalesOrderById(@PathVariable Long id) {

        log.info("GET /api/sales-orders/{} - Fetching sales order", id);

        try {
            SalesOrderDTO order = salesOrderService.getSalesOrderById(id);

            return ResponseEntity.ok(
                    ApiResponse.<SalesOrderDTO>builder()
                            .success(true)
                            .message(messageService.get("sales_order.retrieved.success"))
                            .data(order)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to fetch sales order {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.<SalesOrderDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(404)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Get sales order by order number
     * GET /api/sales-orders/number/{orderNumber}
     */
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<ApiResponse<SalesOrderDTO>> getSalesOrderByNumber(@PathVariable String orderNumber) {

        log.info("GET /api/sales-orders/number/{} - Fetching sales order", orderNumber);

        try {
            SalesOrderDTO order = salesOrderService.getSalesOrderByNumber(orderNumber);

            return ResponseEntity.ok(
                    ApiResponse.<SalesOrderDTO>builder()
                            .success(true)
                            .message(messageService.get("sales_order.retrieved.success"))
                            .data(order)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to fetch sales order {}: {}", orderNumber, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.<SalesOrderDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(404)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Get sales orders by customer
     * GET /api/sales-orders/customer/{customerId}
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<Page<SalesOrderDTO>>> getSalesOrdersByCustomer(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("GET /api/sales-orders/customer/{} - Fetching orders for customer", customerId);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<SalesOrderDTO> orders = salesOrderService.getSalesOrdersByCustomer(customerId, pageable);

            return ResponseEntity.ok(
                    ApiResponse.<Page<SalesOrderDTO>>builder()
                            .success(true)
                            .message(messageService.get("sales_order.retrieved.success"))
                            .data(orders)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to fetch orders for customer {}: {}", customerId, e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<Page<SalesOrderDTO>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Search sales orders
     * GET /api/sales-orders/search?query=SO-2026&page=0&size=20
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<SalesOrderDTO>>> searchSalesOrders(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("GET /api/sales-orders/search - Searching with query: {}", query);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<SalesOrderDTO> orders = salesOrderService.searchSalesOrders(query, pageable);

            return ResponseEntity.ok(
                    ApiResponse.<Page<SalesOrderDTO>>builder()
                            .success(true)
                            .message(messageService.get("sales_order.search.success"))
                            .data(orders)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Search failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<Page<SalesOrderDTO>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Update sales order
     * PUT /api/sales-orders/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SalesOrderDTO>> updateSalesOrder(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSalesOrderRequest request) {

        log.info("PUT /api/sales-orders/{} - Updating sales order", id);

        try {
            SalesOrderDTO updated = salesOrderService.updateSalesOrder(id, request);

            return ResponseEntity.ok(
                    ApiResponse.<SalesOrderDTO>builder()
                            .success(true)
                            .message(messageService.get("sales_order.updated.success"))
                            .data(updated)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to update sales order {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<SalesOrderDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Update sales order status
     * PUT /api/sales-orders/{id}/status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<SalesOrderDTO>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody SalesOrderStatusUpdateRequest request,
            Authentication authentication) {

        log.info("PUT /api/sales-orders/{}/status - Updating status to: {}", id, request.getStatusCode());

        try {
            Long userId = getUserIdFromAuthentication(authentication);
            SalesOrderDTO updated = salesOrderService.updateStatus(id, request, userId);

            return ResponseEntity.ok(
                    ApiResponse.<SalesOrderDTO>builder()
                            .success(true)
                            .message(messageService.get("sales_order.status.updated.success"))
                            .data(updated)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to update status for sales order {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<SalesOrderDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Delete sales order (soft delete)
     * DELETE /api/sales-orders/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSalesOrder(@PathVariable Long id) {

        log.info("DELETE /api/sales-orders/{} - Deleting sales order", id);

        try {
            salesOrderService.deleteSalesOrder(id);

            return ResponseEntity.ok(
                    ApiResponse.<Void>builder()
                            .success(true)
                            .message(messageService.get("sales_order.deleted.success"))
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to delete sales order {}: {}", id, e.getMessage());
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

    /**
     * Helper method to extract user ID from authentication
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
        // You may need to implement this based on your User entity
        // For now, return a default or extract from principal
        return 4L; // Default admin ID
    }
}