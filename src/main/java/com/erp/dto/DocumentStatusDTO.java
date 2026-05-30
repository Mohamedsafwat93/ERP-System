package com.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentStatusDTO {
    private Long id;
    private String documentType;
    private String code;
    private String nameAr;
    private String nameEn;
    private Integer sequenceOrder;
    private Boolean isFinal;
    private Boolean requiresApproval;
    private String colorCode;
    private Boolean isActive;
}