package com.erp.repository;

import com.erp.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // ✅ BASIC QUERIES

    /**
     * Find customer by ID (excluding soft-deleted)
     */
    @Query("SELECT c FROM Customer c WHERE c.id = :id AND c.isDeleted = false")
    Optional<Customer> findByIdAndNotDeleted(@Param("id") Long id);

    /**
     * Find customer by email (excluding soft-deleted)
     */
    @Query("SELECT c FROM Customer c WHERE c.email = :email AND c.isDeleted = false")
    Optional<Customer> findByEmailAndNotDeleted(@Param("email") String email);

    /**
     * Check if email exists (excluding soft-deleted)
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Customer c WHERE c.email = :email AND c.isDeleted = false")
    boolean existsByEmailAndNotDeleted(@Param("email") String email);


    // ✅ SEARCH & FILTER QUERIES

    /**
     * Search customers by name (partial match, case-insensitive)
     */
    @Query("SELECT c FROM Customer c WHERE c.name ILIKE CONCAT('%', :name, '%') AND c.branchId = :branchId AND c.isDeleted = false")
    Page<Customer> searchByNameAndBranch(
            @Param("name") String name,
            @Param("branchId") Long branchId,
            Pageable pageable
    );

    /**
     * Find all customers by branch (paginated, excluding soft-deleted)
     */
    @Query("SELECT c FROM Customer c WHERE c.branchId = :branchId AND c.isDeleted = false ORDER BY c.createdAt DESC")
    Page<Customer> findByBranchIdAndNotDeleted(
            @Param("branchId") Long branchId,
            Pageable pageable
    );

    /**
     * Find active customers by branch
     */
    @Query("SELECT c FROM Customer c WHERE c.branchId = :branchId AND c.isActive = true AND c.isDeleted = false")
    List<Customer> findActiveCustmersByBranch(@Param("branchId") Long branchId);

    /**
     * Search by phone number
     */
    @Query("SELECT c FROM Customer c WHERE c.phone LIKE CONCAT('%', :phone, '%') AND c.branchId = :branchId AND c.isDeleted = false")
    List<Customer> findByPhoneAndBranch(
            @Param("phone") String phone,
            @Param("branchId") Long branchId
    );


    // ✅ ANALYSIS QUERIES

    /**
     * Count total customers by branch
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.branchId = :branchId AND c.isDeleted = false")
    long countByBranch(@Param("branchId") Long branchId);

    /**
     * Get customers with credit limit usage
     */
    @Query("SELECT c FROM Customer c WHERE c.creditLimit > 0 AND c.branchId = :branchId AND c.isDeleted = false")
    List<Customer> findCustomersWithCreditLimit(@Param("branchId") Long branchId);

    /**
     * Find recently added customers
     */
    @Query(value = "SELECT c FROM Customer c WHERE c.branchId = :branchId AND c.isDeleted = false ORDER BY c.createdAt DESC LIMIT :limit")
    List<Customer> findRecentCustomers(
            @Param("branchId") Long branchId,
            @Param("limit") int limit
    );
}