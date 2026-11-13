package com.foodsave.platform.feedback.infrastructure.persistence.jpa;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.foodsave.platform.feedback.domain.model.Review;

public interface ReviewJpaRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByReservationId(Long reservationId);
}
