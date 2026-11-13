package com.foodsave.platform.businesses.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import com.foodsave.platform.businesses.domain.model.Business;

public interface BusinessJpaRepository extends JpaRepository<Business, Long> {
    java.util.List<Business> findByOwnerAccountId(Long accountId);
}
