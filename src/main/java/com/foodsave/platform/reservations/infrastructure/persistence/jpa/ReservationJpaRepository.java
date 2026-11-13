package com.foodsave.platform.reservations.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import com.foodsave.platform.reservations.domain.model.Reservation;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {
    java.util.List<Reservation> findByCustomerUserIdOrderByCreatedAtDesc(Long customerUserId);
    java.util.List<Reservation> findByBusinessIdOrderByCreatedAtDesc(Long businessId);
    java.util.Optional<Reservation> findByPickupCodeIgnoreCase(String pickupCode);
    java.util.List<Reservation> findByStatusAndPickupDeadlineAtBetween(String status, java.time.Instant start, java.time.Instant end);
    java.util.List<Reservation> findByOfferIdAndStatus(Long offerId, String status);
}
