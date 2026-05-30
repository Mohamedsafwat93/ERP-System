package com.erp.repository;

import com.erp.entity.WasteReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WasteReportRepository extends JpaRepository<WasteReport, Long> {

    Optional<WasteReport> findByReportNumber(String reportNumber);

    Page<WasteReport> findByBranchId(Long branchId, Pageable pageable);

    Page<WasteReport> findByWasteTypeId(Long wasteTypeId, Pageable pageable);

    Page<WasteReport> findByIsDeletedFalse(Pageable pageable);

    @Query("SELECT w FROM WasteReport w WHERE w.isDeleted = false AND " +
            "(w.reportNumber LIKE %:search% OR " +
            "w.wasteType.nameAr LIKE %:search% OR " +
            "w.wasteType.nameEn LIKE %:search%)")
    Page<WasteReport> searchWasteReports(@Param("search") String search, Pageable pageable);
}