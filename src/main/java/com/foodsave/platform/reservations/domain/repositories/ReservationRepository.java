package com.foodsave.platform.reservations.domain.repositories;

import java.util.List;
import java.util.Optional;
import com.foodsave.platform.reservations.domain.model.Reservation;

public interface ReservationRepository {
    List<Reservation> findAll();
    Optional<Reservation> findById(Long id);
    List<Reservation> findByCustomer(Long customerId);
    List<Reservation> findByBusiness(Long businessId);
    Optional<Reservation> findByPickupCode(String code);
    List<Reservation> findPickupRemindersDue(java.time.Instant start, java.time.Instant end);
    Reservation save(Reservation reservation);
}
