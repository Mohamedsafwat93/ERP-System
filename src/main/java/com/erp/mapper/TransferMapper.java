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
public class TransferMapper {

    private static final DateTimeFormatter TRANSFER_NUMBER_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public TransferOrderDTO toDTO(TransferOrder transfer) {
        if (transfer == null) return null;

        TransferOrderDTO.TransferOrderDTOBuilder builder = TransferOrderDTO.builder()
                .id(transfer.getId())
                .transferNumber(transfer.getTransferNumber())
                .transferDate(transfer.getTransferDate())
                .expectedDate(transfer.getExpectedDate())
                .receivedDate(transfer.getReceivedDate())
                .transferReason(transfer.getTransferReason())
                .shippingMethod(transfer.getShippingMethod())
                .trackingNumber(transfer.getTrackingNumber())
                .totalValue(transfer.getTotalValue())
                .notes(transfer.getNotes())
                .createdAt(transfer.getCreatedAt())
                .updatedAt(transfer.getUpdatedAt());

        // From Branch info
        if (transfer.getFromBranch() != null) {
            builder.fromBranchId(transfer.getFromBranch().getId())
                    .fromBranchName(transfer.getFromBranch().getName());
        }

        // To Branch info
        if (transfer.getToBranch() != null) {
            builder.toBranchId(transfer.getToBranch().getId())
                    .toBranchName(transfer.getToBranch().getName());
        }

        // From Warehouse info
        if (transfer.getFromWarehouse() != null) {
            builder.fromWarehouseId(transfer.getFromWarehouse().getId())
                    .fromWarehouseName(transfer.getFromWarehouse().getNameAr());
        }

        // To Warehouse info
        if (transfer.getToWarehouse() != null) {
            builder.toWarehouseId(transfer.getToWarehouse().getId())
                    .toWarehouseName(transfer.getToWarehouse().getNameAr());
        }

        // Status info
        if (transfer.getStatus() != null) {
            builder.statusId(transfer.getStatus().getId())
                    .statusCode(transfer.getStatus().getCode())
                    .statusNameAr(transfer.getStatus().getNameAr())
                    .statusNameEn(transfer.getStatus().getNameEn());
        }

        // Approval info
        if (transfer.getRequestedBy() != null) {
            builder.requestedById(transfer.getRequestedBy().getId())
                    .requestedByName(transfer.getRequestedBy().getUsername());
        }
        if (transfer.getApprovedBy() != null) {
            builder.approvedById(transfer.getApprovedBy().getId())
                    .approvedByName(transfer.getApprovedBy().getUsername())
                    .approvedAt(transfer.getApprovedAt());
        }
        if (transfer.getReceivedBy() != null) {
            builder.receivedById(transfer.getReceivedBy().getId())
                    .receivedByName(transfer.getReceivedBy().getUsername());
        }
        if (transfer.getCreatedBy() != null) {
            builder.createdById(transfer.getCreatedBy().getId())
                    .createdByName(transfer.getCreatedBy().getUsername());
        }

        // Items
        if (transfer.getItems() != null && !transfer.getItems().isEmpty()) {
            builder.items(transfer.getItems().stream()
                    .map(this::toItemDTO)
                    .collect(Collectors.toList()));
        } else {
            builder.items(new ArrayList<>());
        }

        return builder.build();
    }

    public TransferItemDTO toItemDTO(TransferItem item) {
        if (item == null) return null;

        TransferItemDTO.TransferItemDTOBuilder builder = TransferItemDTO.builder()
                .id(item.getId())
                .transferOrderId(item.getTransferOrder() != null ? item.getTransferOrder().getId() : null)
                .quantityTransferred(item.getQuantityTransferred())
                .quantityReceived(item.getQuantityReceived())
                .unitCost(item.getUnitCost())
                .totalValue(item.getTotalValue())
                .batchNumber(item.getBatchNumber())
                .expiryDate(item.getExpiryDate())
                .notes(item.getNotes());

        if (item.getProduct() != null) {
            builder.productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .productSku(item.getProduct().getSku());
        }

        if (item.getUom() != null) {
            builder.uomId(item.getUom().getId())
                    .uomCode(item.getUom().getCode())
                    .uomName(item.getUom().getNameAr());
        }

        return builder.build();
    }

    public String generateTransferNumber() {
        return "TRF-" + LocalDateTime.now().format(TRANSFER_NUMBER_FORMATTER);
    }
}