package com.erp.service;

import com.erp.dto.DocumentStatusDTO;
import com.erp.entity.DocumentStatus;
import com.erp.mapper.DocumentStatusMapper;
import com.erp.repository.DocumentStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentStatusService {

    private final DocumentStatusRepository documentStatusRepository;
    private final DocumentStatusMapper documentStatusMapper;  // ← Add this

    @Transactional(readOnly = true)
    public List<DocumentStatusDTO> getStatusesByDocumentType(String documentType) {
        return documentStatusRepository.findByDocumentTypeAndIsActiveTrueOrderBySequenceOrder(documentType)
                .stream()
                .map(documentStatusMapper::toDTO)  // ← Use mapper
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DocumentStatusDTO getStatusByDocumentTypeAndCode(String documentType, String code) {
        DocumentStatus status = documentStatusRepository.findByDocumentTypeAndCode(documentType, code)
                .orElseThrow(() -> new RuntimeException("Status not found: " + documentType + " - " + code));
        return documentStatusMapper.toDTO(status);  // ← Use mapper
    }
}