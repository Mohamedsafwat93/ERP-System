package com.erp.mapper;

import com.erp.dto.*;
import com.erp.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WasteMapper {

    private static final DateTimeFormatter REPORT_NUMBER_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public WasteReportDTO toDTO(WasteReport report) {
        if (report == null) return null;

        WasteReportDTO.WasteReportDTOBuilder builder = WasteReportDTO.builder()
                .id(report.getId())
                .reportNumber(report.getReportNumber())
                .reportDate(report.getReportDate())
                .totalOriginalValue(report.getTotalOriginalValue())
                .totalWriteOffValue(report.getTotalWriteOffValue())
                .rejectionReason(report.getRejectionReason())
                .notes(report.getNotes())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt());

        // Branch info
        if (report.getBranch() != null) {
            builder.branchId(report.getBranch().getId())
                    .branchName(report.getBranch().getName());
        }

        // Warehouse info
        if (report.getWarehouse() != null) {
            builder.warehouseId(report.getWarehouse().getId())
                    .warehouseName(report.getWarehouse().getNameAr());
        }

        // Status info
        if (report.getStatus() != null) {
            builder.statusId(report.getStatus().getId())
                    .statusCode(report.getStatus().getCode())
                    .statusNameAr(report.getStatus().getNameAr())
                    .statusNameEn(report.getStatus().getNameEn());
        }

        // Waste Type info
        if (report.getWasteType() != null) {
            builder.wasteTypeId(report.getWasteType().getId())
                    .wasteTypeCode(report.getWasteType().getCode())
                    .wasteTypeNameAr(report.getWasteType().getNameAr())
                    .wasteTypeNameEn(report.getWasteType().getNameEn());
        }

        // Approval info
        if (report.getReportedBy() != null) {
            builder.reportedById(report.getReportedBy().getId())
                    .reportedByName(report.getReportedBy().getUsername());
        }
        if (report.getApprovedBy() != null) {
            builder.approvedById(report.getApprovedBy().getId())
                    .approvedByName(report.getApprovedBy().getUsername())
                    .approvedAt(report.getApprovedAt());
        }
        if (report.getCreatedBy() != null) {
            builder.createdById(report.getCreatedBy().getId())
                    .createdByName(report.getCreatedBy().getUsername());
        }

        // Items
        if (report.getItems() != null && !report.getItems().isEmpty()) {
            builder.items(report.getItems().stream()
                    .map(this::toItemDTO)
                    .collect(Collectors.toList()));
        } else {
            builder.items(new ArrayList<>());
        }

        return builder.build();
    }

    public WasteItemDTO toItemDTO(WasteItem item) {
        if (item == null) return null;

        return WasteItemDTO.builder()
                .id(item.getId())
                .wasteReportId(item.getWasteReport() != null ? item.getWasteReport().getId() : null)
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .productName(item.getProduct() != null ? item.getProduct().getName() : null)
                .productSku(item.getProduct() != null ? item.getProduct().getSku() : null)
                .uomId(item.getUom() != null ? item.getUom().getId() : null)
                .uomCode(item.getUom() != null ? item.getUom().getCode() : null)
                .uomName(item.getUom() != null ? item.getUom().getNameAr() : null)
                .quantity(item.getQuantity())
                .originalUnitCost(item.getOriginalUnitCost())
                .originalTotalValue(item.getOriginalTotalValue())
                .writeOffValue(item.getWriteOffValue())
                .batchNumber(item.getBatchNumber())
                .expiryDate(item.getExpiryDate())
                .conditionAtDiscovery(item.getConditionAtDiscovery())
                .disposalMethod(item.getDisposalMethod())
                .notes(item.getNotes())
                .build();
    }

    public String generateReportNumber() {
        return "WST-" + LocalDateTime.now().format(REPORT_NUMBER_FORMATTER);
    }
}