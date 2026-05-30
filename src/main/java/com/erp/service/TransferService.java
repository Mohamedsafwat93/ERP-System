package com.erp.service;

import com.erp.dto.*;
import com.erp.entity.*;
import com.erp.mapper.TransferMapper;
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
public class TransferService {

    private final TransferOrderRepository transferOrderRepository;
    private final TransferItemRepository transferItemRepository;
    private final BranchRepository branchRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final DocumentStatusRepository documentStatusRepository;
    private final UserRepository userRepository;
    private final TransferMapper transferMapper;

    @Transactional
    public TransferOrderDTO createTransfer(CreateTransferRequest request, Long userId) {
        log.info("========== STARTING TRANSFER CREATION ==========");

        // 1. Validate branches/warehouses
        Branch fromBranch = null;
        Branch toBranch = null;
        Warehouse fromWarehouse = null;
        Warehouse toWarehouse = null;

        if (request.getFromBranchId() != null) {
            fromBranch = branchRepository.findById(request.getFromBranchId())
                    .orElseThrow(() -> new RuntimeException("From branch not found"));
        }
        if (request.getToBranchId() != null) {
            toBranch = branchRepository.findById(request.getToBranchId())
                    .orElseThrow(() -> new RuntimeException("To branch not found"));
        }
        if (request.getFromWarehouseId() != null) {
            fromWarehouse = warehouseRepository.findById(request.getFromWarehouseId())
                    .orElseThrow(() -> new RuntimeException("From warehouse not found"));
        }
        if (request.getToWarehouseId() != null) {
            toWarehouse = warehouseRepository.findById(request.getToWarehouseId())
                    .orElseThrow(() -> new RuntimeException("To warehouse not found"));
        }

        // 2. Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Get status (DRAFT)
        DocumentStatus draftStatus = documentStatusRepository
                .findByDocumentTypeAndCode("TRANSFER", "DRAFT")
                .orElseThrow(() -> new RuntimeException("DRAFT status not found"));

        // 4. Generate transfer number
        String transferNumber = transferMapper.generateTransferNumber();

        // 5. Calculate total value
        BigDecimal totalValue = BigDecimal.ZERO;
        List<TransferItem> itemsToSave = new ArrayList<>();

        for (CreateTransferItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            BigDecimal itemTotal = itemReq.getUnitCost() != null ?
                    itemReq.getUnitCost().multiply(BigDecimal.valueOf(itemReq.getQuantity())) : BigDecimal.ZERO;
            totalValue = totalValue.add(itemTotal);

            TransferItem item = TransferItem.builder()
                    .product(product)
                    .uom(product.getUnitOfMeasure())
                    .quantityTransferred(itemReq.getQuantity())
                    .quantityReceived(0)
                    .unitCost(itemReq.getUnitCost())
                    .totalValue(itemTotal)
                    .batchNumber(itemReq.getBatchNumber())
                    .notes(itemReq.getNotes())
                    .build();

            itemsToSave.add(item);
            log.info("Item: Product={}, Qty={}, Total={}", product.getName(), itemReq.getQuantity(), itemTotal);
        }

        // 6. Create transfer order
        TransferOrder transfer = TransferOrder.builder()
                .transferNumber(transferNumber)
                .fromBranch(fromBranch)
                .toBranch(toBranch)
                .fromWarehouse(fromWarehouse)
                .toWarehouse(toWarehouse)
                .status(draftStatus)
                .requestedBy(user)
                .createdBy(user)
                .transferDate(LocalDateTime.now())
                .expectedDate(request.getExpectedDate())
                .transferReason(request.getTransferReason())
                .shippingMethod(request.getShippingMethod())
                .trackingNumber(request.getTrackingNumber())
                .totalValue(totalValue)
                .notes(request.getNotes())
                .isDeleted(false)
                .build();

        // 7. Save transfer
        TransferOrder savedTransfer = transferOrderRepository.save(transfer);
        log.info("Transfer saved with ID: {}, Number: {}", savedTransfer.getId(), savedTransfer.getTransferNumber());

        // 8. Save items
        List<TransferItem> savedItems = new ArrayList<>();
        for (TransferItem item : itemsToSave) {
            item.setTransferOrder(savedTransfer);
            TransferItem savedItem = transferItemRepository.save(item);
            savedItems.add(savedItem);
            log.info("Item saved: Product={}", item.getProduct().getName());
        }

        // 9. Set items to transfer
        savedTransfer.setItems(savedItems);

        log.info("========== TRANSFER CREATED SUCCESSFULLY ==========");
        log.info("Transfer ID: {}, Total Value: {}", savedTransfer.getId(), totalValue);

        return transferMapper.toDTO(savedTransfer);
    }

    @Transactional(readOnly = true)
    public Page<TransferOrderDTO> getAllTransfers(Pageable pageable) {
        Page<TransferOrder> transfers = transferOrderRepository.findByIsDeletedFalse(pageable);
        // Load items for each transfer
        transfers.forEach(t -> t.getItems().size());
        return transfers.map(transferMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public TransferOrderDTO getTransferById(Long id) {
        TransferOrder transfer = transferOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfer not found: " + id));
        // Force load items
        transfer.getItems().size();
        return transferMapper.toDTO(transfer);
    }

    @Transactional(readOnly = true)
    public TransferOrderDTO getTransferByNumber(String transferNumber) {
        TransferOrder transfer = transferOrderRepository.findByTransferNumber(transferNumber)
                .orElseThrow(() -> new RuntimeException("Transfer not found: " + transferNumber));
        transfer.getItems().size();
        return transferMapper.toDTO(transfer);
    }

    @Transactional(readOnly = true)
    public Page<TransferOrderDTO> searchTransfers(String search, Pageable pageable) {
        Page<TransferOrder> transfers = transferOrderRepository.searchTransfers(search, pageable);
        transfers.forEach(t -> t.getItems().size());
        return transfers.map(transferMapper::toDTO);
    }

    @Transactional
    public TransferOrderDTO updateStatus(Long id, TransferStatusUpdateRequest request, Long userId) {
        log.info("Updating transfer status: {} to {}", id, request.getStatusCode());

        TransferOrder transfer = transferOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfer not found: " + id));

        DocumentStatus newStatus = documentStatusRepository
                .findByDocumentTypeAndCode("TRANSFER", request.getStatusCode())
                .orElseThrow(() -> new RuntimeException("Status not found: " + request.getStatusCode()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        transfer.setStatus(newStatus);

        if ("APPROVED".equals(request.getStatusCode())) {
            transfer.setApprovedBy(user);
            transfer.setApprovedAt(LocalDateTime.now());
        }

        TransferOrder saved = transferOrderRepository.save(transfer);
        log.info("Transfer status updated to: {}", request.getStatusCode());

        return transferMapper.toDTO(saved);
    }

    @Transactional
    public TransferOrderDTO receiveTransfer(Long id, ReceiveTransferRequest request, Long userId) {
        log.info("Receiving transfer: {}", id);

        TransferOrder transfer = transferOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfer not found: " + id));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update received quantities
        for (ReceiveTransferItemRequest itemReq : request.getItems()) {
            TransferItem item = transferItemRepository.findById(itemReq.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found: " + itemReq.getItemId()));
            item.setQuantityReceived(itemReq.getQuantityReceived());
            transferItemRepository.save(item);
        }

        // Check if all items received
        boolean allReceived = transfer.getItems().stream()
                .allMatch(item -> item.getQuantityReceived() >= item.getQuantityTransferred());

        if (allReceived) {
            DocumentStatus receivedStatus = documentStatusRepository
                    .findByDocumentTypeAndCode("TRANSFER", "RECEIVED")
                    .orElseThrow(() -> new RuntimeException("RECEIVED status not found"));
            transfer.setStatus(receivedStatus);
            transfer.setReceivedDate(LocalDateTime.now());
            transfer.setReceivedBy(user);
            transferOrderRepository.save(transfer);
        }

        log.info("Transfer received: {}", transfer.getTransferNumber());

        // Refresh transfer with items
        TransferOrder refreshedTransfer = transferOrderRepository.findById(id).orElse(transfer);
        refreshedTransfer.getItems().size();

        return transferMapper.toDTO(refreshedTransfer);
    }

    @Transactional
    public void deleteTransfer(Long id) {
        TransferOrder transfer = transferOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transfer not found: " + id));
        transfer.setIsDeleted(true);
        transferOrderRepository.save(transfer);
        log.info("Transfer soft deleted: {}", transfer.getTransferNumber());
    }
}