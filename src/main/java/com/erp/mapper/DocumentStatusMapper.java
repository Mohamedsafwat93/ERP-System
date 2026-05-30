package com.erp.mapper;

import com.erp.dto.DocumentStatusDTO;
import com.erp.entity.DocumentStatus;
import org.springframework.stereotype.Component;

@Component
public class DocumentStatusMapper {

    public DocumentStatusDTO toDTO(DocumentStatus status) {
        if (status == null) return null;

        return DocumentStatusDTO.builder()
                .id(status.getId())
                .documentType(status.getDocumentType())
                .code(status.getCode())
                .nameAr(status.getNameAr())
                .nameEn(status.getNameEn())
                .sequenceOrder(status.getSequenceOrder())
                .isFinal(status.getIsFinal())
                .requiresApproval(status.getRequiresApproval())
                .colorCode(status.getColorCode())
                .isActive(status.getIsActive())
                .build();
    }

    public DocumentStatus toEntity(DocumentStatusDTO dto) {
        if (dto == null) return null;

        return DocumentStatus.builder()
                .id(dto.getId())
                .documentType(dto.getDocumentType())
                .code(dto.getCode())
                .nameAr(dto.getNameAr())
                .nameEn(dto.getNameEn())
                .sequenceOrder(dto.getSequenceOrder())
                .isFinal(dto.getIsFinal())
                .requiresApproval(dto.getRequiresApproval())
                .colorCode(dto.getColorCode())
                .isActive(dto.getIsActive())
                .build();
    }
}