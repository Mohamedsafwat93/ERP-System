package com.erp.repository;

import com.erp.entity.SalesOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {

    Optional<SalesOrder> findByOrderNumber(String orderNumber);

    Page<SalesOrder> findByCustomerId(Long customerId, Pageable pageable);

    Page<SalesOrder> findByBranchId(Long branchId, Pageable pageable);

    Page<SalesOrder> findByStatusId(Long statusId, Pageable pageable);

    Page<SalesOrder> findByIsDeletedFalse(Pageable pageable);

    @Query("SELECT s FROM SalesOrder s WHERE s.isDeleted = false AND " +
            "(s.orderNumber LIKE %:search% OR " +
            "s.customer.name LIKE %:search% OR " +
            "s.customer.email LIKE %:search%)")
    Page<SalesOrder> searchSalesOrders(@Param("search") String search, Pageable pageable);

    List<SalesOrder> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}