package com.erp.repository;

import com.erp.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    Optional<Invoice> findBySalesOrderId(Long salesOrderId);

    Page<Invoice> findByCustomerId(Long customerId, Pageable pageable);

    Page<Invoice> findByIsDeletedFalse(Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE i.isDeleted = false AND " +
            "(i.invoiceNumber LIKE %:search% OR " +
            "i.customer.name LIKE %:search% OR " +
            "i.customer.email LIKE %:search%)")
    Page<Invoice> searchInvoices(@Param("search") String search, Pageable pageable);
}