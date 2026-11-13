package com.foodsave.platform.reservations.infrastructure.persistence.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.foodsave.platform.reservations.domain.model.Reservation;
import com.foodsave.platform.reservations.domain.repositories.ReservationRepository;

@Repository
public class ReservationRepositoryAdapter implements ReservationRepository {
    private final ReservationJpaRepository repository;

    public ReservationRepositoryAdapter(ReservationJpaRepository repository) { this.repository = repository; }
    public List<Reservation> findAll() { return repository.findAll(); }
    public Optional<Reservation> findById(Long id) { return repository.findById(id); }
    public List<Reservation> findByCustomer(Long customerId) { return repository.findByCustomerUserIdOrderByCreatedAtDesc(customerId); }
    public List<Reservation> findByBusiness(Long businessId) { return repository.findByBusinessIdOrderByCreatedAtDesc(businessId); }
    public Optional<Reservation> findByPickupCode(String code) { return repository.findByPickupCodeIgnoreCase(code); }
    public List<Reservation> findPickupRemindersDue(java.time.Instant start, java.time.Instant end) { return repository.findByStatusAndPickupDeadlineAtBetween("ACTIVE", start, end); }
    public Reservation save(Reservation reservation) { return repository.save(reservation); }
}
