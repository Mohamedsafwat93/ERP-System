package com.erp.controller;

import com.erp.dto.*;
import com.erp.service.PurchaseOrderService;
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
import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> createPurchaseOrder(
            @Valid @RequestBody CreatePurchaseOrderRequest request,
            Authentication authentication) {

        log.info("POST /api/purchase-orders - Creating purchase order");

        try {
            Long userId = getUserIdFromAuthentication(authentication);
            PurchaseOrderDTO created = purchaseOrderService.createPurchaseOrder(request, userId);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponse.<PurchaseOrderDTO>builder()
                            .success(true)
                            .message(messageService.get("purchase_order.created.success"))
                            .data(created)
                            .code(201)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to create purchase order: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<PurchaseOrderDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PurchaseOrderDTO>>> getAllPurchaseOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("GET /api/purchase-orders - Fetching all purchase orders");

        try {
            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<PurchaseOrderDTO> orders = purchaseOrderService.getAllPurchaseOrders(pageable);

            return ResponseEntity.ok(
                    ApiResponse.<Page<PurchaseOrderDTO>>builder()
                            .success(true)
                            .message(messageService.get("purchase_order.retrieved.success"))
                            .data(orders)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to fetch purchase orders: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<Page<PurchaseOrderDTO>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> getPurchaseOrderById(@PathVariable Long id) {
        log.info("GET /api/purchase-orders/{} - Fetching purchase order", id);

        try {
            PurchaseOrderDTO order = purchaseOrderService.getPurchaseOrderById(id);
            return ResponseEntity.ok(
                    ApiResponse.<PurchaseOrderDTO>builder()
                            .success(true)
                            .message(messageService.get("purchase_order.retrieved.success"))
                            .data(order)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to fetch purchase order {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.<PurchaseOrderDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(404)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    @GetMapping("/number/{poNumber}")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> getPurchaseOrderByNumber(@PathVariable String poNumber) {
        log.info("GET /api/purchase-orders/number/{} - Fetching purchase order", poNumber);

        try {
            PurchaseOrderDTO order = purchaseOrderService.getPurchaseOrderByNumber(poNumber);
            return ResponseEntity.ok(
                    ApiResponse.<PurchaseOrderDTO>builder()
                            .success(true)
                            .message(messageService.get("purchase_order.retrieved.success"))
                            .data(order)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to fetch purchase order {}: {}", poNumber, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.<PurchaseOrderDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(404)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<ApiResponse<Page<PurchaseOrderDTO>>> getPurchaseOrdersBySupplier(
            @PathVariable Long supplierId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("GET /api/purchase-orders/supplier/{} - Fetching orders for supplier", supplierId);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<PurchaseOrderDTO> orders = purchaseOrderService.getPurchaseOrdersBySupplier(supplierId, pageable);
            return ResponseEntity.ok(
                    ApiResponse.<Page<PurchaseOrderDTO>>builder()
                            .success(true)
                            .message(messageService.get("purchase_order.retrieved.success"))
                            .data(orders)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to fetch orders for supplier {}: {}", supplierId, e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<Page<PurchaseOrderDTO>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<PurchaseOrderDTO>>> searchPurchaseOrders(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("GET /api/purchase-orders/search - Searching with query: {}", query);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<PurchaseOrderDTO> orders = purchaseOrderService.searchPurchaseOrders(query, pageable);
            return ResponseEntity.ok(
                    ApiResponse.<Page<PurchaseOrderDTO>>builder()
                            .success(true)
                            .message(messageService.get("purchase_order.search.success"))
                            .data(orders)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        } catch (Exception e) {
            log.error("Search failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<Page<PurchaseOrderDTO>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> updatePurchaseOrder(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePurchaseOrderRequest request) {

        log.info("PUT /api/purchase-orders/{} - Updating purchase order", id);

        try {
            PurchaseOrderDTO updated = purchaseOrderService.updatePurchaseOrder(id, request);
            return ResponseEntity.ok(
                    ApiResponse.<PurchaseOrderDTO>builder()
                            .success(true)
                            .message(messageService.get("purchase_order.updated.success"))
                            .data(updated)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to update purchase order {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<PurchaseOrderDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody PurchaseOrderStatusUpdateRequest request,
            Authentication authentication) {

        log.info("PUT /api/purchase-orders/{}/status - Updating status to: {}", id, request.getStatusCode());

        try {
            Long userId = getUserIdFromAuthentication(authentication);
            PurchaseOrderDTO updated = purchaseOrderService.updateStatus(id, request, userId);
            return ResponseEntity.ok(
                    ApiResponse.<PurchaseOrderDTO>builder()
                            .success(true)
                            .message(messageService.get("purchase_order.status.updated.success"))
                            .data(updated)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to update status for purchase order {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<PurchaseOrderDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    @PutMapping("/{id}/receive")
    public ResponseEntity<ApiResponse<PurchaseOrderDTO>> receiveItems(
            @PathVariable Long id,
            @RequestBody List<ReceiveItemRequest> receiveRequests) {

        log.info("PUT /api/purchase-orders/{}/receive - Receiving items", id);

        try {
            PurchaseOrderDTO updated = purchaseOrderService.receiveItems(id, receiveRequests);
            return ResponseEntity.ok(
                    ApiResponse.<PurchaseOrderDTO>builder()
                            .success(true)
                            .message(messageService.get("purchase_order.received.success"))
                            .data(updated)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to receive items for purchase order {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<PurchaseOrderDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePurchaseOrder(@PathVariable Long id) {
        log.info("DELETE /api/purchase-orders/{} - Deleting purchase order", id);

        try {
            purchaseOrderService.deletePurchaseOrder(id);
            return ResponseEntity.ok(
                    ApiResponse.<Void>builder()
                            .success(true)
                            .message(messageService.get("purchase_order.deleted.success"))
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to delete purchase order {}: {}", id, e.getMessage());
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

    private Long getUserIdFromAuthentication(Authentication authentication) {
        return 4L;
    }
}