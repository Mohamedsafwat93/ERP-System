package com.erp.repository;

import com.erp.entity.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long> {

    Optional<ProductInventory> findByProductIdAndBranchId(Long productId, Long branchId);

    List<ProductInventory> findByBranchId(Long branchId);

    List<ProductInventory> findByProductId(Long productId);

    @Modifying
    @Transactional
    @Query("UPDATE ProductInventory pi SET pi.quantityOnHand = pi.quantityOnHand + :quantity WHERE pi.product.id = :productId AND pi.branch.id = :branchId")
    void addStock(@Param("productId") Long productId, @Param("branchId") Long branchId, @Param("quantity") Integer quantity);

    @Modifying
    @Transactional
    @Query("UPDATE ProductInventory pi SET pi.quantityOnHand = pi.quantityOnHand - :quantity WHERE pi.product.id = :productId AND pi.branch.id = :branchId")
    void removeStock(@Param("productId") Long productId, @Param("branchId") Long branchId, @Param("quantity") Integer quantity);

    @Query("SELECT pi FROM ProductInventory pi WHERE pi.quantityOnHand <= pi.reorderPoint AND pi.quantityOnHand > 0")
    List<ProductInventory> findLowStockProducts();
}