package com.erp.controller;

import com.erp.dto.ApiResponse;
import com.erp.dto.WasteTypeDTO;
import com.erp.service.WasteTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/waste-types")
@RequiredArgsConstructor
@Slf4j
public class WasteTypeController {

    private final WasteTypeService wasteTypeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<WasteTypeDTO>>> getAllWasteTypes() {
        List<WasteTypeDTO> wasteTypes = wasteTypeService.getAllWasteTypes();
        return ResponseEntity.ok(ApiResponse.<List<WasteTypeDTO>>builder()
                .success(true)
                .message("Waste types retrieved successfully")
                .data(wasteTypes)
                .code(200)
                .build());
    }

    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<WasteTypeDTO>> getWasteTypeByCode(@PathVariable String code) {
        WasteTypeDTO wasteType = wasteTypeService.getWasteTypeByCode(code);
        return ResponseEntity.ok(ApiResponse.<WasteTypeDTO>builder()
                .success(true)
                .message("Waste type retrieved successfully")
                .data(wasteType)
                .code(200)
                .build());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<WasteTypeDTO>>> getWasteTypesByCategory(@PathVariable String category) {
        List<WasteTypeDTO> wasteTypes = wasteTypeService.getWasteTypesByCategory(category);
        return ResponseEntity.ok(ApiResponse.<List<WasteTypeDTO>>builder()
                .success(true)
                .message("Waste types retrieved by category")
                .data(wasteTypes)
                .code(200)
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WasteTypeDTO>> createWasteType(@Valid @RequestBody WasteTypeDTO dto) {
        WasteTypeDTO created = wasteTypeService.createWasteType(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<WasteTypeDTO>builder()
                        .success(true)
                        .message("Waste type created successfully")
                        .data(created)
                        .code(201)
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WasteTypeDTO>> updateWasteType(
            @PathVariable Long id,
            @Valid @RequestBody WasteTypeDTO dto) {
        WasteTypeDTO updated = wasteTypeService.updateWasteType(id, dto);
        return ResponseEntity.ok(ApiResponse.<WasteTypeDTO>builder()
                .success(true)
                .message("Waste type updated successfully")
                .data(updated)
                .code(200)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWasteType(@PathVariable Long id) {
        wasteTypeService.deleteWasteType(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Waste type deleted successfully")
                .code(200)
                .build());
    }
}