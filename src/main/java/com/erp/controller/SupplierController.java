package com.erp.controller;

import com.erp.dto.ApiResponse;
import com.erp.dto.CreateSupplierRequest;
import com.erp.dto.SupplierResponse;
import com.erp.dto.UpdateSupplierRequest;
import com.erp.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    // CREATE
    @PostMapping
    public ResponseEntity<ApiResponse<SupplierResponse>> createSupplier(
            @Valid @RequestBody CreateSupplierRequest request) {
        try {
            Long branchId = 1L;
            SupplierResponse response = supplierService.createSupplier(request, branchId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<SupplierResponse>builder()
                            .success(true)
                            .message("Supplier created successfully")
                            .data(response)
                            .code(201)
                            .lang("en")
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.<SupplierResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .code(409)
                            .lang("en")
                            .build());
        }
    }

    // GET ONE
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierResponse>> getSupplier(@PathVariable Long id) {
        try {
            SupplierResponse response = supplierService.getSupplierById(id);
            return ResponseEntity.ok(ApiResponse.<SupplierResponse>builder()
                    .success(true)
                    .message("Supplier retrieved")
                    .data(response)
                    .code(200)
                    .lang("en")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<SupplierResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .code(404)
                            .lang("en")
                            .build());
        }
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<ApiResponse<Page<SupplierResponse>>> getAllSuppliers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Long branchId = 1L;
            Pageable pageable = PageRequest.of(page, size);
            Page<SupplierResponse> response = supplierService.getAllSuppliers(branchId, pageable);
            return ResponseEntity.ok(ApiResponse.<Page<SupplierResponse>>builder()
                    .success(true)
                    .message("Suppliers retrieved")
                    .data(response)
                    .code(200)
                    .lang("en")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Page<SupplierResponse>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .code(500)
                            .lang("en")
                            .build());
        }
    }

    // SEARCH
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SupplierResponse>>> searchSuppliers(
            @RequestParam String query) {
        try {
            Long branchId = 1L;
            List<SupplierResponse> response = supplierService.searchSuppliers(query, branchId);
            return ResponseEntity.ok(ApiResponse.<List<SupplierResponse>>builder()
                    .success(true)
                    .message("Search completed")
                    .data(response)
                    .code(200)
                    .lang("en")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<List<SupplierResponse>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .code(400)
                            .lang("en")
                            .build());
        }
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierResponse>> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSupplierRequest request) {
        try {
            SupplierResponse response = supplierService.updateSupplier(id, request);
            return ResponseEntity.ok(ApiResponse.<SupplierResponse>builder()
                    .success(true)
                    .message("Supplier updated")
                    .data(response)
                    .code(200)
                    .lang("en")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<SupplierResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .code(404)
                            .lang("en")
                            .build());
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        try {
            supplierService.deleteSupplier(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // GET ACTIVE SUPPLIERS
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<SupplierResponse>>> getActiveSuppliers() {
        try {
            Long branchId = 1L;
            List<SupplierResponse> response = supplierService.getActiveSuppliers(branchId);
            return ResponseEntity.ok(ApiResponse.<List<SupplierResponse>>builder()
                    .success(true)
                    .message("Active suppliers retrieved")
                    .data(response)
                    .code(200)
                    .lang("en")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<SupplierResponse>>builder()
                            .success(false)
                            .message(e.getMessage())
                            .data(null)
                            .code(500)
                            .lang("en")
                            .build());
        }
    }
}