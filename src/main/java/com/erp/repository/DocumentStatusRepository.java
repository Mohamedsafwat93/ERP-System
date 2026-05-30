package com.erp.repository;

import com.erp.entity.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentStatusRepository extends JpaRepository<DocumentStatus, Long> {

    List<DocumentStatus> findByDocumentTypeOrderBySequenceOrder(String documentType);

    Optional<DocumentStatus> findByDocumentTypeAndCode(String documentType, String code);

    List<DocumentStatus> findByDocumentTypeAndIsActiveTrueOrderBySequenceOrder(String documentType);

    List<DocumentStatus> findByDocumentTypeAndRequiresApprovalTrue(String documentType);
}