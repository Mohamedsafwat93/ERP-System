package com.erp.controller;

import com.erp.dto.*;
import com.erp.service.InvoiceService;
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

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Slf4j
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final MessageService messageService;

    /**
     * Create invoice from sales order
     * POST /api/invoices/from-sales-order
     */
    @PostMapping("/from-sales-order")
    public ResponseEntity<ApiResponse<InvoiceDTO>> createInvoiceFromSalesOrder(
            @Valid @RequestBody CreateInvoiceFromSalesOrderRequest request,
            Authentication authentication) {

        log.info("POST /api/invoices/from-sales-order - Creating invoice from sales order: {}", request.getSalesOrderId());

        try {
            Long userId = getUserIdFromAuthentication(authentication);
            InvoiceDTO created = invoiceService.createInvoiceFromSalesOrder(request, userId);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponse.<InvoiceDTO>builder()
                            .success(true)
                            .message(messageService.get("invoice.created.success"))
                            .data(created)
                            .code(201)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to create invoice: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<InvoiceDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Get all invoices (paginated)
     * GET /api/invoices?page=0&size=20&sortBy=invoiceDate&sortDir=desc
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<InvoiceDTO>>> getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "invoiceDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("GET /api/invoices - Fetching all invoices");

        try {
            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<InvoiceDTO> invoices = invoiceService.getAllInvoices(pageable);

            return ResponseEntity.ok(
                    ApiResponse.<Page<InvoiceDTO>>builder()
                            .success(true)
                            .message(messageService.get("invoice.retrieved.success"))
                            .data(invoices)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to fetch invoices: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<Page<InvoiceDTO>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Get invoice by ID
     * GET /api/invoices/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceDTO>> getInvoiceById(@PathVariable Long id) {

        log.info("GET /api/invoices/{} - Fetching invoice", id);

        try {
            InvoiceDTO invoice = invoiceService.getInvoiceById(id);

            return ResponseEntity.ok(
                    ApiResponse.<InvoiceDTO>builder()
                            .success(true)
                            .message(messageService.get("invoice.retrieved.success"))
                            .data(invoice)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to fetch invoice {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.<InvoiceDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(404)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Get invoice by invoice number
     * GET /api/invoices/number/{invoiceNumber}
     */
    @GetMapping("/number/{invoiceNumber}")
    public ResponseEntity<ApiResponse<InvoiceDTO>> getInvoiceByNumber(@PathVariable String invoiceNumber) {

        log.info("GET /api/invoices/number/{} - Fetching invoice", invoiceNumber);

        try {
            InvoiceDTO invoice = invoiceService.getInvoiceByNumber(invoiceNumber);

            return ResponseEntity.ok(
                    ApiResponse.<InvoiceDTO>builder()
                            .success(true)
                            .message(messageService.get("invoice.retrieved.success"))
                            .data(invoice)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to fetch invoice {}: {}", invoiceNumber, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.<InvoiceDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(404)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Get invoices by customer
     * GET /api/invoices/customer/{customerId}
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<Page<InvoiceDTO>>> getInvoicesByCustomer(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("GET /api/invoices/customer/{} - Fetching invoices for customer", customerId);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<InvoiceDTO> invoices = invoiceService.getInvoicesByCustomer(customerId, pageable);

            return ResponseEntity.ok(
                    ApiResponse.<Page<InvoiceDTO>>builder()
                            .success(true)
                            .message(messageService.get("invoice.retrieved.success"))
                            .data(invoices)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to fetch invoices for customer {}: {}", customerId, e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<Page<InvoiceDTO>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Search invoices
     * GET /api/invoices/search?query=INV-2026&page=0&size=20
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<InvoiceDTO>>> searchInvoices(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("GET /api/invoices/search - Searching with query: {}", query);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<InvoiceDTO> invoices = invoiceService.searchInvoices(query, pageable);

            return ResponseEntity.ok(
                    ApiResponse.<Page<InvoiceDTO>>builder()
                            .success(true)
                            .message(messageService.get("invoice.search.success"))
                            .data(invoices)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Search failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<Page<InvoiceDTO>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Record payment for invoice
     * POST /api/invoices/{id}/payments
     */
    @PostMapping("/{id}/payments")
    public ResponseEntity<ApiResponse<InvoiceDTO>> recordPayment(
            @PathVariable Long id,
            @Valid @RequestBody RecordPaymentRequest request,
            Authentication authentication) {

        log.info("POST /api/invoices/{}/payments - Recording payment", id);

        try {
            Long userId = getUserIdFromAuthentication(authentication);
            InvoiceDTO updated = invoiceService.recordPayment(id, request, userId);

            return ResponseEntity.ok(
                    ApiResponse.<InvoiceDTO>builder()
                            .success(true)
                            .message(messageService.get("invoice.payment.recorded.success"))
                            .data(updated)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to record payment for invoice {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<InvoiceDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Delete invoice (soft delete)
     * DELETE /api/invoices/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteInvoice(@PathVariable Long id) {

        log.info("DELETE /api/invoices/{} - Deleting invoice", id);

        try {
            invoiceService.deleteInvoice(id);

            return ResponseEntity.ok(
                    ApiResponse.<Void>builder()
                            .success(true)
                            .message(messageService.get("invoice.deleted.success"))
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to delete invoice {}: {}", id, e.getMessage());
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