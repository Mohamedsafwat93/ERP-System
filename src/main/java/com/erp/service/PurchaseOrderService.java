package com.erp.service;

import com.erp.dto.*;
import com.erp.entity.*;
import com.erp.mapper.PurchaseOrderMapper;
import com.erp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final SupplierRepository supplierRepository;
    private final BranchRepository branchRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final DocumentStatusRepository documentStatusRepository;
    private final UserRepository userRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;

    @Transactional
    public PurchaseOrderDTO createPurchaseOrder(CreatePurchaseOrderRequest request, Long userId) {
        log.info("========== STARTING PURCHASE ORDER CREATION ==========");

        // 1. Get all required entities
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        DocumentStatus draftStatus = documentStatusRepository
                .findByDocumentTypeAndCode("PURCHASE_ORDER", "DRAFT")
                .orElseThrow(() -> new RuntimeException("DRAFT status not found"));

        // 2. Calculate subtotal from items
        BigDecimal subtotal = BigDecimal.ZERO;
        List<CreatePurchaseOrderItemRequest> itemRequests = request.getItems();

        for (CreatePurchaseOrderItemRequest itemReq : itemRequests) {
            BigDecimal lineSubtotal = itemReq.getUnitCost().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            subtotal = subtotal.add(lineSubtotal);
            log.info("Item - Qty: {}, UnitCost: {}, LineSubtotal: {}", itemReq.getQuantity(), itemReq.getUnitCost(), lineSubtotal);
        }
        log.info("Calculated Subtotal: {}", subtotal);

        // 3. Calculate discount
        BigDecimal discountAmount = request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO;
        log.info("Discount Amount: {}", discountAmount);

        // 4. Calculate tax
        BigDecimal afterDiscount = subtotal.subtract(discountAmount);
        BigDecimal taxAmount = afterDiscount.multiply(new BigDecimal("0.15"));
        log.info("Calculated Tax: {}", taxAmount);

        // 5. Calculate total
        BigDecimal shippingCost = request.getShippingCost() != null ? request.getShippingCost() : BigDecimal.ZERO;
        BigDecimal totalAmount = afterDiscount.add(taxAmount).add(shippingCost);
        log.info("Calculated Total: {}", totalAmount);

        // 6. Generate PO number
        String poNumber = purchaseOrderMapper.generatePONumber();
        log.info("PO Number: {}", poNumber);

        // 7. Create PO with all calculated values
        PurchaseOrder order = PurchaseOrder.builder()
                .poNumber(poNumber)
                .supplier(supplier)
                .branch(branch)
                .status(draftStatus)
                .requestedBy(user)
                .createdBy(user)
                .orderDate(LocalDateTime.now())
                .expectedDate(request.getExpectedDate())
                .discountAmount(discountAmount)
                .subtotal(subtotal)
                .taxRate(new BigDecimal("15.00"))
                .taxAmount(taxAmount)
                .shippingCost(shippingCost)
                .totalAmount(totalAmount)
                .paymentTerms(request.getPaymentTerms())
                .notes(request.getNotes())
                .isDeleted(false)
                .build();

        // 8. Save order
        PurchaseOrder savedOrder = purchaseOrderRepository.save(order);
        log.info("PO saved with ID: {}, Total: {}", savedOrder.getId(), savedOrder.getTotalAmount());

        // 9. Create and save items
        List<PurchaseOrderItem> savedItems = new ArrayList<>();

        for (CreatePurchaseOrderItemRequest itemReq : itemRequests) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            BigDecimal lineSubtotal = itemReq.getUnitCost().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            BigDecimal itemDiscountPercent = itemReq.getDiscountPercentage() != null ? itemReq.getDiscountPercentage() : BigDecimal.ZERO;
            BigDecimal itemDiscountAmount = lineSubtotal.multiply(itemDiscountPercent.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
            BigDecimal afterItemDiscount = lineSubtotal.subtract(itemDiscountAmount);
            BigDecimal itemTaxAmount = afterItemDiscount.multiply(new BigDecimal("0.15"));
            BigDecimal itemTotal = afterItemDiscount.add(itemTaxAmount);

            PurchaseOrderItem item = PurchaseOrderItem.builder()
                    .purchaseOrder(savedOrder)
                    .product(product)
                    .uom(product.getUnitOfMeasure())
                    .quantity(itemReq.getQuantity())
                    .quantityReceived(0)
                    .unitCost(itemReq.getUnitCost())
                    .discountPercentage(itemDiscountPercent)
                    .discountAmount(itemDiscountAmount)
                    .taxRate(new BigDecimal("15.00"))
                    .taxAmount(itemTaxAmount)
                    .totalCost(itemTotal)
                    .notes(itemReq.getNotes())
                    .build();

            PurchaseOrderItem savedItem = purchaseOrderItemRepository.save(item);
            savedItems.add(savedItem);
            log.info("Item saved: Product={}, Total={}", product.getName(), itemTotal);
        }

        // 10. Set items to order
        savedOrder.setItems(savedItems);

        log.info("========== PURCHASE ORDER CREATED SUCCESSFULLY ==========");
        log.info("PO ID: {}, Total Items: {}", savedOrder.getId(), savedItems.size());

        return purchaseOrderMapper.toDTO(savedOrder);
    }

    @Transactional(readOnly = true)
    public Page<PurchaseOrderDTO> getAllPurchaseOrders(Pageable pageable) {
        return purchaseOrderRepository.findByIsDeletedFalse(pageable)
                .map(purchaseOrderMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public PurchaseOrderDTO getPurchaseOrderById(Long id) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found: " + id));
        order.getItems().size();
        return purchaseOrderMapper.toDTO(order);
    }

    @Transactional(readOnly = true)
    public PurchaseOrderDTO getPurchaseOrderByNumber(String poNumber) {
        PurchaseOrder order = purchaseOrderRepository.findByPoNumber(poNumber)
                .orElseThrow(() -> new RuntimeException("Purchase order not found: " + poNumber));
        order.getItems().size();
        return purchaseOrderMapper.toDTO(order);
    }

    @Transactional(readOnly = true)
    public Page<PurchaseOrderDTO> getPurchaseOrdersBySupplier(Long supplierId, Pageable pageable) {
        return purchaseOrderRepository.findBySupplierId(supplierId, pageable)
                .map(purchaseOrderMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<PurchaseOrderDTO> searchPurchaseOrders(String search, Pageable pageable) {
        return purchaseOrderRepository.searchPurchaseOrders(search, pageable)
                .map(purchaseOrderMapper::toDTO);
    }

    @Transactional
    public PurchaseOrderDTO updatePurchaseOrder(Long id, UpdatePurchaseOrderRequest request) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found: " + id));

        if (request.getWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                    .orElseThrow(() -> new RuntimeException("Warehouse not found"));
            order.setWarehouse(warehouse);
        }
        if (request.getExpectedDate() != null) order.setExpectedDate(request.getExpectedDate());
        if (request.getDiscountAmount() != null) order.setDiscountAmount(request.getDiscountAmount());
        if (request.getShippingCost() != null) order.setShippingCost(request.getShippingCost());
        if (request.getPaymentTerms() != null) order.setPaymentTerms(request.getPaymentTerms());
        if (request.getNotes() != null) order.setNotes(request.getNotes());

        PurchaseOrder saved = purchaseOrderRepository.save(order);
        return purchaseOrderMapper.toDTO(saved);
    }

    @Transactional
    public PurchaseOrderDTO updateStatus(Long id, PurchaseOrderStatusUpdateRequest request, Long userId) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found: " + id));

        DocumentStatus newStatus = documentStatusRepository
                .findByDocumentTypeAndCode("PURCHASE_ORDER", request.getStatusCode())
                .orElseThrow(() -> new RuntimeException("Status not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        order.setStatus(newStatus);

        if ("APPROVED".equals(request.getStatusCode())) {
            order.setApprovedBy(user);
            order.setApprovedAt(LocalDateTime.now());
        } else if ("RECEIVED".equals(request.getStatusCode())) {
            order.setReceivedDate(LocalDateTime.now());
        }

        PurchaseOrder saved = purchaseOrderRepository.save(order);
        return purchaseOrderMapper.toDTO(saved);
    }

    @Transactional
    public void deletePurchaseOrder(Long id) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found: " + id));
        order.setIsDeleted(true);
        purchaseOrderRepository.save(order);
    }

    @Transactional
    public PurchaseOrderDTO receiveItems(Long id, List<ReceiveItemRequest> receiveRequests) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found: " + id));

        for (ReceiveItemRequest receiveReq : receiveRequests) {
            PurchaseOrderItem item = purchaseOrderItemRepository.findById(receiveReq.getItemId())
                    .orElseThrow(() -> new RuntimeException("Item not found"));
            item.setQuantityReceived(receiveReq.getQuantityReceived());
            purchaseOrderItemRepository.save(item);
        }

        // Check if all items received
        boolean allReceived = order.getItems().stream()
                .allMatch(item -> item.getQuantityReceived() >= item.getQuantity());

        if (allReceived) {
            DocumentStatus receivedStatus = documentStatusRepository
                    .findByDocumentTypeAndCode("PURCHASE_ORDER", "FULLY_RECEIVED")
                    .orElseThrow(() -> new RuntimeException("FULLY_RECEIVED status not found"));
            order.setStatus(receivedStatus);
            order.setReceivedDate(LocalDateTime.now());
            purchaseOrderRepository.save(order);
        }

        return purchaseOrderMapper.toDTO(order);
    }
}