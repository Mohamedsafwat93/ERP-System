package com.erp.repository;

import com.erp.entity.TransferItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransferItemRepository extends JpaRepository<TransferItem, Long> {

    List<TransferItem> findByTransferOrderId(Long transferOrderId);

    List<TransferItem> findByProductId(Long productId);
}