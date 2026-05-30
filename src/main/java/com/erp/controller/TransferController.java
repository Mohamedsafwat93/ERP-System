package com.erp.controller;

import com.erp.dto.*;
import com.erp.service.TransferService;
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
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@Slf4j
public class TransferController {

    private final TransferService transferService;
    private final MessageService messageService;

    /**
     * Create transfer order
     * POST /api/transfers
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TransferOrderDTO>> createTransfer(
            @Valid @RequestBody CreateTransferRequest request,
            Authentication authentication) {

        log.info("POST /api/transfers - Creating transfer order");

        try {
            Long userId = getUserIdFromAuthentication(authentication);
            TransferOrderDTO created = transferService.createTransfer(request, userId);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponse.<TransferOrderDTO>builder()
                            .success(true)
                            .message(messageService.get("transfer.created.success"))
                            .data(created)
                            .code(201)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to create transfer: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<TransferOrderDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Get all transfers (paginated)
     * GET /api/transfers?page=0&size=20&sortBy=transferDate&sortDir=desc
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TransferOrderDTO>>> getAllTransfers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "transferDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("GET /api/transfers - Fetching all transfers");

        try {
            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<TransferOrderDTO> transfers = transferService.getAllTransfers(pageable);

            return ResponseEntity.ok(
                    ApiResponse.<Page<TransferOrderDTO>>builder()
                            .success(true)
                            .message(messageService.get("transfer.retrieved.success"))
                            .data(transfers)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to fetch transfers: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<Page<TransferOrderDTO>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Get transfer by ID
     * GET /api/transfers/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransferOrderDTO>> getTransferById(@PathVariable Long id) {

        log.info("GET /api/transfers/{} - Fetching transfer", id);

        try {
            TransferOrderDTO transfer = transferService.getTransferById(id);

            return ResponseEntity.ok(
                    ApiResponse.<TransferOrderDTO>builder()
                            .success(true)
                            .message(messageService.get("transfer.retrieved.success"))
                            .data(transfer)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to fetch transfer {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.<TransferOrderDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(404)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Get transfer by transfer number
     * GET /api/transfers/number/{transferNumber}
     */
    @GetMapping("/number/{transferNumber}")
    public ResponseEntity<ApiResponse<TransferOrderDTO>> getTransferByNumber(@PathVariable String transferNumber) {

        log.info("GET /api/transfers/number/{} - Fetching transfer", transferNumber);

        try {
            TransferOrderDTO transfer = transferService.getTransferByNumber(transferNumber);

            return ResponseEntity.ok(
                    ApiResponse.<TransferOrderDTO>builder()
                            .success(true)
                            .message(messageService.get("transfer.retrieved.success"))
                            .data(transfer)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to fetch transfer {}: {}", transferNumber, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.<TransferOrderDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(404)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Search transfers
     * GET /api/transfers/search?query=TRF-2026&page=0&size=20
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<TransferOrderDTO>>> searchTransfers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("GET /api/transfers/search - Searching with query: {}", query);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<TransferOrderDTO> transfers = transferService.searchTransfers(query, pageable);

            return ResponseEntity.ok(
                    ApiResponse.<Page<TransferOrderDTO>>builder()
                            .success(true)
                            .message(messageService.get("transfer.search.success"))
                            .data(transfers)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Search failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<Page<TransferOrderDTO>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Update transfer status
     * PUT /api/transfers/{id}/status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TransferOrderDTO>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody TransferStatusUpdateRequest request,
            Authentication authentication) {

        log.info("PUT /api/transfers/{}/status - Updating status to: {}", id, request.getStatusCode());

        try {
            Long userId = getUserIdFromAuthentication(authentication);
            TransferOrderDTO updated = transferService.updateStatus(id, request, userId);

            return ResponseEntity.ok(
                    ApiResponse.<TransferOrderDTO>builder()
                            .success(true)
                            .message(messageService.get("transfer.status.updated.success"))
                            .data(updated)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to update status for transfer {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<TransferOrderDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Receive transfer items
     * PUT /api/transfers/{id}/receive
     */
    @PutMapping("/{id}/receive")
    public ResponseEntity<ApiResponse<TransferOrderDTO>> receiveTransfer(
            @PathVariable Long id,
            @RequestBody ReceiveTransferRequest request,
            Authentication authentication) {

        log.info("PUT /api/transfers/{}/receive - Receiving transfer", id);

        try {
            Long userId = getUserIdFromAuthentication(authentication);
            TransferOrderDTO updated = transferService.receiveTransfer(id, request, userId);

            return ResponseEntity.ok(
                    ApiResponse.<TransferOrderDTO>builder()
                            .success(true)
                            .message(messageService.get("transfer.received.success"))
                            .data(updated)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to receive transfer {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<TransferOrderDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Delete transfer (soft delete)
     * DELETE /api/transfers/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTransfer(@PathVariable Long id) {

        log.info("DELETE /api/transfers/{} - Deleting transfer", id);

        try {
            transferService.deleteTransfer(id);

            return ResponseEntity.ok(
                    ApiResponse.<Void>builder()
                            .success(true)
                            .message(messageService.get("transfer.deleted.success"))
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to delete transfer {}: {}", id, e.getMessage());
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