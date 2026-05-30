package com.erp.controller;

import com.erp.dto.ApiResponse;
import com.erp.dto.DocumentStatusDTO;
import com.erp.service.DocumentStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/document-status")
@RequiredArgsConstructor
@Slf4j
public class DocumentStatusController {

    private final DocumentStatusService documentStatusService;

    @GetMapping("/{documentType}")
    public ResponseEntity<ApiResponse<List<DocumentStatusDTO>>> getStatusesByDocumentType(
            @PathVariable String documentType) {
        List<DocumentStatusDTO> statuses = documentStatusService.getStatusesByDocumentType(documentType);
        return ResponseEntity.ok(ApiResponse.<List<DocumentStatusDTO>>builder()
                .success(true)
                .message("Document statuses retrieved successfully")
                .data(statuses)
                .code(200)
                .build());
    }

    @GetMapping("/{documentType}/{code}")
    public ResponseEntity<ApiResponse<DocumentStatusDTO>> getStatusByDocumentTypeAndCode(
            @PathVariable String documentType,
            @PathVariable String code) {
        DocumentStatusDTO status = documentStatusService.getStatusByDocumentTypeAndCode(documentType, code);
        return ResponseEntity.ok(ApiResponse.<DocumentStatusDTO>builder()
                .success(true)
                .message("Document status retrieved successfully")
                .data(status)
                .code(200)
                .build());
    }
}