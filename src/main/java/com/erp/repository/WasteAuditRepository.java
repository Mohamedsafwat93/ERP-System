package com.erp.repository;

import com.erp.entity.WasteAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WasteAuditRepository extends JpaRepository<WasteAudit, Long> {

    List<WasteAudit> findByWasteItemId(Long wasteItemId);
}