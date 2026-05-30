package com.erp.repository;

import com.erp.entity.TransferOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TransferOrderRepository extends JpaRepository<TransferOrder, Long> {

    Optional<TransferOrder> findByTransferNumber(String transferNumber);

    Page<TransferOrder> findByFromBranchId(Long branchId, Pageable pageable);

    Page<TransferOrder> findByToBranchId(Long branchId, Pageable pageable);

    Page<TransferOrder> findByStatusId(Long statusId, Pageable pageable);

    Page<TransferOrder> findByIsDeletedFalse(Pageable pageable);

    @Query("SELECT t FROM TransferOrder t WHERE t.isDeleted = false AND " +
            "(t.transferNumber LIKE %:search% OR " +
            "t.fromBranch.name LIKE %:search% OR " +
            "t.toBranch.name LIKE %:search%)")
    Page<TransferOrder> searchTransfers(@Param("search") String search, Pageable pageable);
}