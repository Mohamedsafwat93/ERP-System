package com.erp.repository;

import com.erp.entity.TaxRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaxRateRepository extends JpaRepository<TaxRate, Long> {

    Optional<TaxRate> findByCountryCode(String countryCode);

    List<TaxRate> findByIsActiveTrue();

    Optional<TaxRate> findByIsDefaultTrue();

    List<TaxRate> findByCountryCodeIn(List<String> countryCodes);
}