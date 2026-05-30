package com.erp.repository;

import com.erp.entity.User;
import com.erp.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    // REMOVE these methods - they don't exist in your User entity
    // Page<User> findByIsDeletedFalse(Pageable pageable);
    // Page<User> findByRoleAndIsDeletedFalse(UserRole role, Pageable pageable);
}