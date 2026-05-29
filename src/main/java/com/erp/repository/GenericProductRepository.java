package com.erp.repository;

import com.erp.entity.GenericProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GenericProductRepository extends JpaRepository<GenericProduct, Long> {
    Optional<GenericProduct> findByName(String name);
    Page<GenericProduct> findByIsActiveTrue(Pageable pageable);
    List<GenericProduct> findByParentIdAndIsActiveTrue(Long parentId);
    boolean existsByName(String name);
}