package com.erp.repository;

import com.erp.entity.WasteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WasteItemRepository extends JpaRepository<WasteItem, Long> {

    List<WasteItem> findByWasteReportId(Long wasteReportId);

    List<WasteItem> findByProductId(Long productId);
}