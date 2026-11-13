package com.foodsave.platform.businesses.domain.repositories;

import java.util.List;
import java.util.Optional;
import com.foodsave.platform.businesses.domain.model.Business;

public interface BusinessRepository {
    List<Business> findAll();
    Optional<Business> findById(Long id);
    Business save(Business business);
    List<Business> findByOwner(Long accountId);
}
