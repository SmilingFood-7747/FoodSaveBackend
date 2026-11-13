package com.foodsave.platform.offers.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import com.foodsave.platform.offers.domain.model.Offer;
import com.foodsave.platform.offers.domain.model.OfferStatus;

public interface OfferJpaRepository extends JpaRepository<Offer, Long> {
    java.util.List<Offer> findByBusinessId(Long businessId);
    List<Offer> findByStatusAndExpiresAtLessThanEqual(OfferStatus status, Instant now);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select offer from Offer offer where offer.id = ?1")
    java.util.Optional<Offer> findByIdForUpdate(Long id);

    List<Offer> findByStatusAndAvailableUnitsGreaterThanAndExpiresAtAfter(OfferStatus status, int minimumUnits, Instant now);
}
