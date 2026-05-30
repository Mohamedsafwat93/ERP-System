package com.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WasteReportDTO {
    private Long id;
    private String reportNumber;

    private Long branchId;
    private String branchName;
    private Long warehouseId;
    private String warehouseName;

    private Long statusId;
    private String statusCode;
    private String statusNameAr;
    private String statusNameEn;

    private Long wasteTypeId;
    private String wasteTypeCode;
    private String wasteTypeNameAr;
    private String wasteTypeNameEn;

    private LocalDateTime reportDate;
    private BigDecimal totalOriginalValue;
    private BigDecimal totalWriteOffValue;

    private Long reportedById;
    private String reportedByName;
    private Long approvedById;
    private String approvedByName;
    private LocalDateTime approvedAt;
    private String rejectionReason;

    private List<WasteItemDTO> items;

    private String notes;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}