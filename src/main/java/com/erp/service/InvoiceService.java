package com.erp.service;

import com.erp.dto.*;
import com.erp.entity.*;
import com.erp.mapper.InvoiceMapper;
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
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderItemRepository salesOrderItemRepository;
    private final CustomerRepository customerRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    private final UnitOfMeasureRepository uomRepository;
    private final DocumentStatusRepository documentStatusRepository;
    private final UserRepository userRepository;
    private final InvoiceMapper invoiceMapper;

    @Transactional
    public InvoiceDTO createInvoiceFromSalesOrder(CreateInvoiceFromSalesOrderRequest request, Long userId) {
        log.info("========== STARTING INVOICE CREATION FROM SALES ORDER ==========");

        // 1. Get sales order
        SalesOrder salesOrder = salesOrderRepository.findById(request.getSalesOrderId())
                .orElseThrow(() -> new RuntimeException("Sales order not found: " + request.getSalesOrderId()));

        // Check if invoice already exists for this sales order
        if (invoiceRepository.findBySalesOrderId(salesOrder.getId()).isPresent()) {
            throw new RuntimeException("Invoice already exists for this sales order");
        }

        // 2. Get customer and branch
        Customer customer = salesOrder.getCustomer();
        Branch branch = salesOrder.getBranch();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Get status (DRAFT for invoice)
        DocumentStatus draftStatus = documentStatusRepository
                .findByDocumentTypeAndCode("INVOICE", "DRAFT")
                .orElseThrow(() -> new RuntimeException("DRAFT status not found"));

        // 4. Generate invoice number
        String invoiceNumber = invoiceMapper.generateInvoiceNumber();

        // 5. Create invoice with values from sales order
        Invoice invoice = Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .salesOrder(salesOrder)
                .customer(customer)
                .branch(branch)
                .status(draftStatus)
                .createdBy(user)
                .invoiceDate(LocalDateTime.now())
                .dueDate(request.getDueDate() != null ? request.getDueDate() : LocalDateTime.now().plusDays(30).toLocalDate())
                .subtotal(salesOrder.getSubtotal())
                .discountAmount(salesOrder.getDiscountAmount())
                .taxRate(salesOrder.getTaxRate())
                .taxAmount(salesOrder.getTaxAmount())
                .totalAmount(salesOrder.getTotalAmount())
                .amountPaid(BigDecimal.ZERO)
                .balanceDue(salesOrder.getTotalAmount())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus("UNPAID")
                .notes(request.getNotes())
                .isDeleted(false)
                .build();

        // 6. Save invoice
        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice saved with ID: {}, Number: {}", savedInvoice.getId(), savedInvoice.getInvoiceNumber());

        // 7. Create invoice items from sales order items
        List<InvoiceItem> savedItems = new ArrayList<>();

        for (SalesOrderItem orderItem : salesOrder.getItems()) {
            InvoiceItem item = InvoiceItem.builder()
                    .invoice(savedInvoice)
                    .salesOrderItem(orderItem)
                    .product(orderItem.getProduct())
                    .uom(orderItem.getUom())
                    .quantity(orderItem.getQuantity())
                    .unitPrice(orderItem.getUnitPrice())
                    .discountPercentage(orderItem.getDiscountPercentage())
                    .discountAmount(orderItem.getDiscountAmount())
                    .taxRate(orderItem.getTaxRate())
                    .taxAmount(orderItem.getTaxAmount())
                    .totalPrice(orderItem.getTotalPrice())
                    .notes(orderItem.getNotes())
                    .build();

            InvoiceItem savedItem = invoiceItemRepository.save(item);
            savedItems.add(savedItem);
            log.info("Invoice item saved: Product={}, Total={}", orderItem.getProduct().getName(), orderItem.getTotalPrice());
        }

        // 8. Set items to invoice
        savedInvoice.setItems(savedItems);

        log.info("========== INVOICE CREATED SUCCESSFULLY ==========");
        log.info("Invoice ID: {}, Total Amount: {}", savedInvoice.getId(), savedInvoice.getTotalAmount());

        return invoiceMapper.toDTO(savedInvoice);
    }

    @Transactional(readOnly = true)
    public Page<InvoiceDTO> getAllInvoices(Pageable pageable) {
        return invoiceRepository.findByIsDeletedFalse(pageable)
                .map(invoiceMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public InvoiceDTO getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + id));
        invoice.getItems().size();
        return invoiceMapper.toDTO(invoice);
    }

    @Transactional(readOnly = true)
    public InvoiceDTO getInvoiceByNumber(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + invoiceNumber));
        invoice.getItems().size();
        return invoiceMapper.toDTO(invoice);
    }

    @Transactional(readOnly = true)
    public Page<InvoiceDTO> getInvoicesByCustomer(Long customerId, Pageable pageable) {
        return invoiceRepository.findByCustomerId(customerId, pageable)
                .map(invoiceMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<InvoiceDTO> searchInvoices(String search, Pageable pageable) {
        return invoiceRepository.searchInvoices(search, pageable)
                .map(invoiceMapper::toDTO);
    }

    @Transactional
    public InvoiceDTO recordPayment(Long id, RecordPaymentRequest request, Long userId) {
        log.info("Recording payment for invoice: {}", id);

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + id));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update payment amounts
        BigDecimal newAmountPaid = invoice.getAmountPaid().add(request.getAmount());
        invoice.setAmountPaid(newAmountPaid);
        invoice.setBalanceDue(invoice.getTotalAmount().subtract(newAmountPaid));
        invoice.setPaymentMethod(request.getPaymentMethod());

        // Update payment status
        if (invoice.getBalanceDue().compareTo(BigDecimal.ZERO) <= 0) {
            invoice.setPaymentStatus("PAID");

            // Update invoice status to PAID
            DocumentStatus paidStatus = documentStatusRepository
                    .findByDocumentTypeAndCode("INVOICE", "PAID")
                    .orElseThrow(() -> new RuntimeException("PAID status not found"));
            invoice.setStatus(paidStatus);
        } else {
            invoice.setPaymentStatus("PARTIAL");

            // Update invoice status to PARTIAL_PAID
            DocumentStatus partialStatus = documentStatusRepository
                    .findByDocumentTypeAndCode("INVOICE", "PARTIAL_PAID")
                    .orElseThrow(() -> new RuntimeException("PARTIAL_PAID status not found"));
            invoice.setStatus(partialStatus);
        }

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        log.info("Payment recorded: Invoice {}, Amount paid: {}, New balance: {}",
                invoice.getInvoiceNumber(), request.getAmount(), updatedInvoice.getBalanceDue());

        return invoiceMapper.toDTO(updatedInvoice);
    }

    @Transactional
    public void deleteInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + id));
        invoice.setIsDeleted(true);
        invoiceRepository.save(invoice);
        log.info("Invoice soft deleted: {}", invoice.getInvoiceNumber());
    }
}