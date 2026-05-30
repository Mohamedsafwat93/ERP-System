package com.erp.repository;

import com.erp.entity.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    Optional<PurchaseOrder> findByPoNumber(String poNumber);

    Page<PurchaseOrder> findBySupplierId(Long supplierId, Pageable pageable);

    Page<PurchaseOrder> findByBranchId(Long branchId, Pageable pageable);

    Page<PurchaseOrder> findByStatusId(Long statusId, Pageable pageable);

    Page<PurchaseOrder> findByIsDeletedFalse(Pageable pageable);

    @Query("SELECT p FROM PurchaseOrder p WHERE p.isDeleted = false AND " +
            "(p.poNumber LIKE %:search% OR " +
            "p.supplier.name LIKE %:search% OR " +
            "p.supplier.email LIKE %:search%)")
    Page<PurchaseOrder> searchPurchaseOrders(@Param("search") String search, Pageable pageable);
}