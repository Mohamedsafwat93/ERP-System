package com.erp.repository;

import com.erp.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findByIdAndIsDeletedFalse(Long id);

    Optional<Supplier> findByEmailAndIsDeletedFalse(String email);

    boolean existsByEmailAndIsDeletedFalse(String email);

    Page<Supplier> findByBranchIdAndIsDeletedFalse(Long branchId, Pageable pageable);

    List<Supplier> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name);

    List<Supplier> findByBranchIdAndIsActiveTrueAndIsDeletedFalse(Long branchId);


}