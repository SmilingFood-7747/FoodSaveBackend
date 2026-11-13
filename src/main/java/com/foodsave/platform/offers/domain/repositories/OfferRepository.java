package com.foodsave.platform.offers.domain.repositories;

import java.util.List;
import java.util.Optional;
import com.foodsave.platform.offers.domain.model.Offer;

public interface OfferRepository {
    List<Offer> findAll();
    Optional<Offer> findById(Long id);
    Optional<Offer> findByIdForUpdate(Long id);
    Offer save(Offer offer);
    List<Offer> findActiveAt(java.time.Instant now);
    List<Offer> findByBusinessId(Long businessId);
    List<Offer> findExpiredAt(java.time.Instant now);
    void deleteById(Long id);
}
