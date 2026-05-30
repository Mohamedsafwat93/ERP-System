package com.erp.controller;

import com.erp.dto.*;
import com.erp.service.WasteService;
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
@RequestMapping("/api/waste")
@RequiredArgsConstructor
@Slf4j
public class WasteController {

    private final WasteService wasteService;
    private final MessageService messageService;

    /**
     * Create waste report
     * POST /api/waste/reports
     */
    @PostMapping("/reports")
    public ResponseEntity<ApiResponse<WasteReportDTO>> createWasteReport(
            @Valid @RequestBody CreateWasteReportRequest request,
            Authentication authentication) {

        log.info("POST /api/waste/reports - Creating waste report");

        try {
            Long userId = getUserIdFromAuthentication(authentication);
            WasteReportDTO created = wasteService.createWasteReport(request, userId);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponse.<WasteReportDTO>builder()
                            .success(true)
                            .message(messageService.get("waste.created.success"))
                            .data(created)
                            .code(201)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to create waste report: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<WasteReportDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Get all waste reports (paginated)
     * GET /api/waste/reports?page=0&size=20&sortBy=reportDate&sortDir=desc
     */
    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<Page<WasteReportDTO>>> getAllWasteReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "reportDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("GET /api/waste/reports - Fetching all waste reports");

        try {
            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<WasteReportDTO> reports = wasteService.getAllWasteReports(pageable);

            return ResponseEntity.ok(
                    ApiResponse.<Page<WasteReportDTO>>builder()
                            .success(true)
                            .message(messageService.get("waste.retrieved.success"))
                            .data(reports)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to fetch waste reports: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<Page<WasteReportDTO>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Get waste report by ID
     * GET /api/waste/reports/{id}
     */
    @GetMapping("/reports/{id}")
    public ResponseEntity<ApiResponse<WasteReportDTO>> getWasteReportById(@PathVariable Long id) {

        log.info("GET /api/waste/reports/{} - Fetching waste report", id);

        try {
            WasteReportDTO report = wasteService.getWasteReportById(id);

            return ResponseEntity.ok(
                    ApiResponse.<WasteReportDTO>builder()
                            .success(true)
                            .message(messageService.get("waste.retrieved.success"))
                            .data(report)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to fetch waste report {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.<WasteReportDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(404)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Get waste report by report number
     * GET /api/waste/reports/number/{reportNumber}
     */
    @GetMapping("/reports/number/{reportNumber}")
    public ResponseEntity<ApiResponse<WasteReportDTO>> getWasteReportByNumber(@PathVariable String reportNumber) {

        log.info("GET /api/waste/reports/number/{} - Fetching waste report", reportNumber);

        try {
            WasteReportDTO report = wasteService.getWasteReportByNumber(reportNumber);

            return ResponseEntity.ok(
                    ApiResponse.<WasteReportDTO>builder()
                            .success(true)
                            .message(messageService.get("waste.retrieved.success"))
                            .data(report)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to fetch waste report {}: {}", reportNumber, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.<WasteReportDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(404)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Search waste reports
     * GET /api/waste/reports/search?query=WST-2026&page=0&size=20
     */
    @GetMapping("/reports/search")
    public ResponseEntity<ApiResponse<Page<WasteReportDTO>>> searchWasteReports(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("GET /api/waste/reports/search - Searching with query: {}", query);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<WasteReportDTO> reports = wasteService.searchWasteReports(query, pageable);

            return ResponseEntity.ok(
                    ApiResponse.<Page<WasteReportDTO>>builder()
                            .success(true)
                            .message(messageService.get("waste.search.success"))
                            .data(reports)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Search failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<Page<WasteReportDTO>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Update waste report status
     * PUT /api/waste/reports/{id}/status
     */
    @PutMapping("/reports/{id}/status")
    public ResponseEntity<ApiResponse<WasteReportDTO>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody WasteStatusUpdateRequest request,
            Authentication authentication) {

        log.info("PUT /api/waste/reports/{}/status - Updating status to: {}", id, request.getStatusCode());

        try {
            Long userId = getUserIdFromAuthentication(authentication);
            WasteReportDTO updated = wasteService.updateStatus(id, request, userId);

            return ResponseEntity.ok(
                    ApiResponse.<WasteReportDTO>builder()
                            .success(true)
                            .message(messageService.get("waste.status.updated.success"))
                            .data(updated)
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to update status for waste report {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(
                    ApiResponse.<WasteReportDTO>builder()
                            .success(false)
                            .message(e.getMessage())
                            .code(400)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );
        }
    }

    /**
     * Delete waste report (soft delete)
     * DELETE /api/waste/reports/{id}
     */
    @DeleteMapping("/reports/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWasteReport(@PathVariable Long id) {

        log.info("DELETE /api/waste/reports/{} - Deleting waste report", id);

        try {
            wasteService.deleteWasteReport(id);

            return ResponseEntity.ok(
                    ApiResponse.<Void>builder()
                            .success(true)
                            .message(messageService.get("waste.deleted.success"))
                            .code(200)
                            .lang(LocaleContextHolder.getLocale().getLanguage())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to delete waste report {}: {}", id, e.getMessage());
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