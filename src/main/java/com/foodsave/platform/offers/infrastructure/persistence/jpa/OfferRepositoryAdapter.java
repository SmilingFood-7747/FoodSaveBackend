package com.foodsave.platform.offers.infrastructure.persistence.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.foodsave.platform.offers.domain.model.Offer;
import com.foodsave.platform.offers.domain.repositories.OfferRepository;

@Repository
public class OfferRepositoryAdapter implements OfferRepository {
    private final OfferJpaRepository repository;

    public OfferRepositoryAdapter(OfferJpaRepository repository) { this.repository = repository; }
    public List<Offer> findAll() { return repository.findAll(); }
    public List<Offer> findByBusinessId(Long businessId) { return repository.findByBusinessId(businessId); }
    public List<Offer> findExpiredAt(java.time.Instant now) { return repository.findByStatusAndExpiresAtLessThanEqual(com.foodsave.platform.offers.domain.model.OfferStatus.ACTIVE, now); }
    public Optional<Offer> findById(Long id) { return repository.findById(id); }
    public Optional<Offer> findByIdForUpdate(Long id) { return repository.findByIdForUpdate(id); }
    public Offer save(Offer offer) { return repository.save(offer); }
    public List<Offer> findActiveAt(java.time.Instant now) {
        return repository.findByStatusAndAvailableUnitsGreaterThanAndExpiresAtAfter(
                com.foodsave.platform.offers.domain.model.OfferStatus.ACTIVE, 0, now);
    }
    public void deleteById(Long id) { repository.deleteById(id); }
}
