package com.foodsave.platform.feedback.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.foodsave.platform.feedback.domain.model.Review;
import com.foodsave.platform.feedback.domain.model.SupportRequest;
import com.foodsave.platform.feedback.infrastructure.persistence.jpa.ReviewJpaRepository;
import com.foodsave.platform.feedback.infrastructure.persistence.jpa.SupportRequestJpaRepository;
import com.foodsave.platform.reservations.application.ReservationService;
import com.foodsave.platform.shared.interfaces.rest.ResourceNotFoundException;

@Service
@Transactional(readOnly = true)
public class FeedbackService {
    private final ReviewJpaRepository reviews;
    private final SupportRequestJpaRepository requests;
    private final ReservationService reservations;
    public FeedbackService(ReviewJpaRepository reviews, SupportRequestJpaRepository requests, ReservationService reservations) { this.reviews = reviews; this.requests = requests; this.reservations = reservations; }
    @Transactional public Review submitReview(Long accountId, Long reservationId, int rating, String comment) {
        var reservation = reservations.get(reservationId);
        if (!reservation.getCustomerUserId().equals(accountId) || !"COLLECTED".equals(reservation.getStatus())) throw new IllegalArgumentException("review.pickup.required");
        if (reviews.findByReservationId(reservationId).isPresent()) throw new IllegalArgumentException("review.already.exists");
        return reviews.save(new Review(reservationId, rating, comment));
    }
    public List<Review> reviews() { return reviews.findAll(); }
    public List<SupportRequest> myRequests(Long accountId) { return requests.findByRequesterAccountIdOrderByCreatedAtDesc(accountId); }
    @Transactional public SupportRequest openRequest(Long accountId, Long reservationId, String subject, String description) {
        if (reservationId != null && !reservations.get(reservationId).getCustomerUserId().equals(accountId)) throw new ResourceNotFoundException("reservation.not.found");
        return requests.save(new SupportRequest(accountId, reservationId, subject, description));
    }
}
