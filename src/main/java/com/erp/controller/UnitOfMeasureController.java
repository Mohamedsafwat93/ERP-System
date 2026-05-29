package com.erp.controller;

import com.erp.dto.ApiResponse;
import com.erp.dto.UnitOfMeasureDTO;
import com.erp.service.UnitOfMeasureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/uoms")
@RequiredArgsConstructor
@Slf4j
public class UnitOfMeasureController {

    private final UnitOfMeasureService uomService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UnitOfMeasureDTO>>> getAllUOMs() {
        List<UnitOfMeasureDTO> uoms = uomService.getAllActiveUOMs();
        return ResponseEntity.ok(ApiResponse.<List<UnitOfMeasureDTO>>builder()
                .success(true)
                .message("Units of measure retrieved successfully")
                .data(uoms)
                .code(200)
                .build());
    }
}