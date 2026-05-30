package com.erp.repository;

import com.erp.entity.WasteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WasteTypeRepository extends JpaRepository<WasteType, Long> {

    Optional<WasteType> findByCode(String code);

    List<WasteType> findByIsActiveTrue();

    List<WasteType> findByCategory(String category);

    boolean existsByCode(String code);
}