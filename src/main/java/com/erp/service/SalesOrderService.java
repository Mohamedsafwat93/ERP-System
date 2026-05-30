package com.erp.service;

import com.erp.dto.*;
import com.erp.entity.*;
import com.erp.mapper.SalesOrderMapper;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderItemRepository salesOrderItemRepository;
    private final CustomerRepository customerRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final DocumentStatusRepository documentStatusRepository;
    private final UserRepository userRepository;
    private final SalesOrderMapper salesOrderMapper;

    private static final DateTimeFormatter ORDER_NUMBER_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Transactional
    public SalesOrderDTO createSalesOrder(CreateSalesOrderRequest request, Long userId) {
        log.info("========== STARTING SALES ORDER CREATION ==========");

        // 1. Get all required entities
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        DocumentStatus draftStatus = documentStatusRepository
                .findByDocumentTypeAndCode("SALES_ORDER", "DRAFT")
                .orElseThrow(() -> new RuntimeException("DRAFT status not found"));

        // 2. Calculate subtotal from items FIRST
        BigDecimal subtotal = BigDecimal.ZERO;
        List<CreateSalesOrderItemRequest> itemRequests = request.getItems();

        for (CreateSalesOrderItemRequest itemReq : itemRequests) {
            BigDecimal lineSubtotal = itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            subtotal = subtotal.add(lineSubtotal);
            log.info("Item - Qty: {}, Price: {}, LineSubtotal: {}", itemReq.getQuantity(), itemReq.getUnitPrice(), lineSubtotal);
        }
        log.info("Calculated Subtotal: {}", subtotal);

        // 3. Calculate discount
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.getDiscountValue() != null && request.getDiscountValue().compareTo(BigDecimal.ZERO) > 0) {
            if ("PERCENTAGE".equals(request.getDiscountType())) {
                discountAmount = subtotal.multiply(request.getDiscountValue().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
            } else {
                discountAmount = request.getDiscountValue();
            }
        }
        log.info("Calculated Discount: {}", discountAmount);

        // 4. Calculate tax
        BigDecimal afterDiscount = subtotal.subtract(discountAmount);
        BigDecimal taxAmount = afterDiscount.multiply(new BigDecimal("0.15"));
        log.info("Calculated Tax: {}", taxAmount);

        // 5. Calculate total
        BigDecimal shippingCost = request.getShippingCost() != null ? request.getShippingCost() : BigDecimal.ZERO;
        BigDecimal totalAmount = afterDiscount.add(taxAmount).add(shippingCost);
        log.info("Calculated Total: {}", totalAmount);

        // 6. Generate order number
        String orderNumber = "SO-" + LocalDateTime.now().format(ORDER_NUMBER_FORMATTER);
        log.info("Order Number: {}", orderNumber);

        // 7. CREATE ORDER WITH ALL CALCULATED VALUES
        SalesOrder order = SalesOrder.builder()
                .orderNumber(orderNumber)
                .customer(customer)
                .branch(branch)
                .status(draftStatus)
                .requestedBy(user)
                .createdBy(user)
                .orderDate(LocalDateTime.now())
                .requiredDate(request.getRequiredDate())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue() != null ? request.getDiscountValue() : BigDecimal.ZERO)
                .discountAmount(discountAmount)
                .subtotal(subtotal)
                .taxRate(new BigDecimal("15.00"))
                .taxAmount(taxAmount)
                .shippingCost(shippingCost)
                .totalAmount(totalAmount)
                .shippingAddress(request.getShippingAddress())
                .notes(request.getNotes())
                .isDeleted(false)
                .build();

        // 8. Save order
        SalesOrder savedOrder = salesOrderRepository.save(order);
        log.info("Order saved with ID: {}, Subtotal: {}, Total: {}", savedOrder.getId(), savedOrder.getSubtotal(), savedOrder.getTotalAmount());

        // 9. Create and save items
        List<SalesOrderItem> savedItems = new ArrayList<>(); // ← COLLECT ITEMS HERE

        for (CreateSalesOrderItemRequest itemReq : itemRequests) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            BigDecimal lineSubtotal = itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            BigDecimal itemDiscountPercent = itemReq.getDiscountPercentage() != null ? itemReq.getDiscountPercentage() : BigDecimal.ZERO;
            BigDecimal itemDiscountAmount = lineSubtotal.multiply(itemDiscountPercent.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
            BigDecimal afterItemDiscount = lineSubtotal.subtract(itemDiscountAmount);
            BigDecimal itemTaxAmount = afterItemDiscount.multiply(new BigDecimal("0.15"));
            BigDecimal itemTotal = afterItemDiscount.add(itemTaxAmount);

            SalesOrderItem item = SalesOrderItem.builder()
                    .salesOrder(savedOrder)
                    .product(product)
                    .uom(product.getUnitOfMeasure())
                    .quantity(itemReq.getQuantity())
                    .quantityShipped(0)
                    .unitPrice(itemReq.getUnitPrice())
                    .discountPercentage(itemDiscountPercent)
                    .discountAmount(itemDiscountAmount)
                    .taxRate(new BigDecimal("15.00"))
                    .taxAmount(itemTaxAmount)
                    .totalPrice(itemTotal)
                    .notes(itemReq.getNotes())
                    .build();

            SalesOrderItem savedItem = salesOrderItemRepository.save(item);
            savedItems.add(savedItem); // ← ADD TO LIST
            log.info("Item saved: Product={}, Total={}", product.getName(), itemTotal);
        }

        // 10. SET ITEMS TO ORDER (THIS FIXES THE EMPTY ITEMS IN RESPONSE)
        savedOrder.setItems(savedItems);

        log.info("========== SALES ORDER CREATED SUCCESSFULLY ==========");
        log.info("Order ID: {}, Total Items: {}", savedOrder.getId(), savedItems.size());

        // 11. Return the complete order with items
        return salesOrderMapper.toDTO(savedOrder);
    }

    @Transactional(readOnly = true)
    public Page<SalesOrderDTO> getAllSalesOrders(Pageable pageable) {
        return salesOrderRepository.findByIsDeletedFalse(pageable)
                .map(salesOrderMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public SalesOrderDTO getSalesOrderById(Long id) {
        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales order not found: " + id));
        order.getItems().size();
        return salesOrderMapper.toDTO(order);
    }

    @Transactional(readOnly = true)
    public SalesOrderDTO getSalesOrderByNumber(String orderNumber) {
        SalesOrder order = salesOrderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Sales order not found: " + orderNumber));
        order.getItems().size();
        return salesOrderMapper.toDTO(order);
    }

    @Transactional(readOnly = true)
    public Page<SalesOrderDTO> getSalesOrdersByCustomer(Long customerId, Pageable pageable) {
        return salesOrderRepository.findByCustomerId(customerId, pageable)
                .map(salesOrderMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<SalesOrderDTO> searchSalesOrders(String search, Pageable pageable) {
        return salesOrderRepository.searchSalesOrders(search, pageable)
                .map(salesOrderMapper::toDTO);
    }

    @Transactional
    public SalesOrderDTO updateSalesOrder(Long id, UpdateSalesOrderRequest request) {
        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales order not found: " + id));
        if (request.getRequiredDate() != null) order.setRequiredDate(request.getRequiredDate());
        if (request.getDiscountValue() != null) order.setDiscountValue(request.getDiscountValue());
        if (request.getShippingCost() != null) order.setShippingCost(request.getShippingCost());
        if (request.getShippingAddress() != null) order.setShippingAddress(request.getShippingAddress());
        if (request.getNotes() != null) order.setNotes(request.getNotes());
        SalesOrder saved = salesOrderRepository.save(order);
        return salesOrderMapper.toDTO(saved);
    }

    @Transactional
    public SalesOrderDTO updateStatus(Long id, SalesOrderStatusUpdateRequest request, Long userId) {
        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales order not found: " + id));
        DocumentStatus newStatus = documentStatusRepository
                .findByDocumentTypeAndCode("SALES_ORDER", request.getStatusCode())
                .orElseThrow(() -> new RuntimeException("Status not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        order.setStatus(newStatus);
        if ("APPROVED".equals(request.getStatusCode())) {
            order.setApprovedBy(user);
            order.setApprovedAt(LocalDateTime.now());
        } else if ("REJECTED".equals(request.getStatusCode())) {
            order.setRejectedBy(user);
            order.setRejectedAt(LocalDateTime.now());
            order.setRejectionReason(request.getRejectionReason());
        }
        SalesOrder saved = salesOrderRepository.save(order);
        return salesOrderMapper.toDTO(saved);
    }

    @Transactional
    public void deleteSalesOrder(Long id) {
        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales order not found: " + id));
        order.setIsDeleted(true);
        salesOrderRepository.save(order);
    }
}