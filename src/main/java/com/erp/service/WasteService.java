package com.erp.service;

import com.erp.dto.*;
import com.erp.entity.*;
import com.erp.mapper.WasteMapper;
import com.erp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WasteService {

    private final WasteReportRepository wasteReportRepository;
    private final WasteItemRepository wasteItemRepository;
    private final BranchRepository branchRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final WasteTypeRepository wasteTypeRepository;
    private final DocumentStatusRepository documentStatusRepository;
    private final UserRepository userRepository;
    private final WasteMapper wasteMapper;

    @Transactional
    public WasteReportDTO createWasteReport(CreateWasteReportRequest request, Long userId) {
        log.info("========== STARTING WASTE REPORT CREATION ==========");

        // 1. Validate branch
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        // 2. Validate warehouse (optional)
        Warehouse warehouse = null;
        if (request.getWarehouseId() != null) {
            warehouse = warehouseRepository.findById(request.getWarehouseId())
                    .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        }

        // 3. Validate waste type
        WasteType wasteType = wasteTypeRepository.findByCode(request.getWasteTypeCode())
                .orElseThrow(() -> new RuntimeException("Waste type not found: " + request.getWasteTypeCode()));

        // 4. Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 5. Get status (DRAFT)
        DocumentStatus draftStatus = documentStatusRepository
                .findByDocumentTypeAndCode("WASTE", "DRAFT")
                .orElseThrow(() -> new RuntimeException("DRAFT status not found"));

        // 6. Generate report number
        String reportNumber = wasteMapper.generateReportNumber();

        // 7. Calculate totals and create items
        BigDecimal totalOriginalValue = BigDecimal.ZERO;
        BigDecimal totalWriteOffValue = BigDecimal.ZERO;
        List<WasteItem> itemsToSave = new ArrayList<>();

        for (CreateWasteItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            BigDecimal originalUnitCost = product.getCostPrice() != null ? product.getCostPrice() : BigDecimal.ZERO;
            BigDecimal originalTotalValue = originalUnitCost.multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            BigDecimal writeOffValue = originalTotalValue; // Full write-off by default

            WasteItem item = WasteItem.builder()
                    .product(product)
                    .uom(product.getUnitOfMeasure())
                    .quantity(itemReq.getQuantity())
                    .originalUnitCost(originalUnitCost)
                    .originalTotalValue(originalTotalValue)
                    .writeOffValue(writeOffValue)
                    .disposalMethod(itemReq.getDisposalMethod())
                    .notes(itemReq.getNotes())
                    .build();

            itemsToSave.add(item);
            totalOriginalValue = totalOriginalValue.add(originalTotalValue);
            totalWriteOffValue = totalWriteOffValue.add(writeOffValue);
            log.info("Item: Product={}, Qty={}, OriginalValue={}", product.getName(), itemReq.getQuantity(), originalTotalValue);
        }

        // 8. Create waste report
        WasteReport report = WasteReport.builder()
                .reportNumber(reportNumber)
                .branch(branch)
                .warehouse(warehouse)
                .wasteType(wasteType)
                .status(draftStatus)
                .reportedBy(user)
                .createdBy(user)
                .reportDate(LocalDateTime.now())
                .totalOriginalValue(totalOriginalValue)
                .totalWriteOffValue(totalWriteOffValue)
                .notes(request.getNotes())
                .isDeleted(false)
                .build();

        // 9. Save report
        WasteReport savedReport = wasteReportRepository.save(report);
        log.info("Waste report saved with ID: {}, Number: {}", savedReport.getId(), savedReport.getReportNumber());

        // 10. Save items
        List<WasteItem> savedItems = new ArrayList<>();
        for (WasteItem item : itemsToSave) {
            item.setWasteReport(savedReport);
            WasteItem savedItem = wasteItemRepository.save(item);
            savedItems.add(savedItem);
            log.info("Item saved: Product={}", item.getProduct().getName());
        }

        // 11. Set items to report
        savedReport.setItems(savedItems);

        log.info("========== WASTE REPORT CREATED SUCCESSFULLY ==========");
        log.info("Report ID: {}, Total Write-off: {}", savedReport.getId(), totalWriteOffValue);

        return wasteMapper.toDTO(savedReport);
    }

    @Transactional(readOnly = true)
    public Page<WasteReportDTO> getAllWasteReports(Pageable pageable) {
        Page<WasteReport> reports = wasteReportRepository.findByIsDeletedFalse(pageable);
        reports.forEach(r -> r.getItems().size());
        return reports.map(wasteMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public WasteReportDTO getWasteReportById(Long id) {
        WasteReport report = wasteReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Waste report not found: " + id));
        report.getItems().size();
        return wasteMapper.toDTO(report);
    }

    @Transactional(readOnly = true)
    public WasteReportDTO getWasteReportByNumber(String reportNumber) {
        WasteReport report = wasteReportRepository.findByReportNumber(reportNumber)
                .orElseThrow(() -> new RuntimeException("Waste report not found: " + reportNumber));
        report.getItems().size();
        return wasteMapper.toDTO(report);
    }

    @Transactional(readOnly = true)
    public Page<WasteReportDTO> searchWasteReports(String search, Pageable pageable) {
        Page<WasteReport> reports = wasteReportRepository.searchWasteReports(search, pageable);
        reports.forEach(r -> r.getItems().size());
        return reports.map(wasteMapper::toDTO);
    }

    @Transactional
    public WasteReportDTO updateStatus(Long id, WasteStatusUpdateRequest request, Long userId) {
        log.info("Updating waste report status: {} to {}", id, request.getStatusCode());

        WasteReport report = wasteReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Waste report not found: " + id));

        DocumentStatus newStatus = documentStatusRepository
                .findByDocumentTypeAndCode("WASTE", request.getStatusCode())
                .orElseThrow(() -> new RuntimeException("Status not found: " + request.getStatusCode()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        report.setStatus(newStatus);

        if ("APPROVED".equals(request.getStatusCode())) {
            report.setApprovedBy(user);
            report.setApprovedAt(LocalDateTime.now());
        } else if ("REJECTED".equals(request.getStatusCode())) {
            report.setRejectionReason(request.getRejectionReason());
        }

        WasteReport saved = wasteReportRepository.save(report);
        log.info("Waste report status updated to: {}", request.getStatusCode());

        return wasteMapper.toDTO(saved);
    }

    @Transactional
    public void deleteWasteReport(Long id) {
        WasteReport report = wasteReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Waste report not found: " + id));
        report.setIsDeleted(true);
        wasteReportRepository.save(report);
        log.info("Waste report soft deleted: {}", report.getReportNumber());
    }
}