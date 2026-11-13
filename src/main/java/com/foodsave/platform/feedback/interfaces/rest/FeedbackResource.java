package com.foodsave.platform.feedback.interfaces.rest;

import java.time.Instant;
import com.foodsave.platform.feedback.domain.model.Review;
import com.foodsave.platform.feedback.domain.model.SupportRequest;

public final class FeedbackResource {
    private FeedbackResource() {}
    public record ReviewView(Long id, Long reservationId, int rating, String comment, Instant createdAt) {
        public static ReviewView from(Review r) { return new ReviewView(r.getId(), r.getReservationId(), r.getRating(), r.getComment(), r.getCreatedAt()); }
    }
    public record SupportView(Long id, Long requesterAccountId, Long reservationId, String subject, String description, String status, Instant createdAt) {
        public static SupportView from(SupportRequest r) { return new SupportView(r.getId(), r.getRequesterAccountId(), r.getReservationId(), r.getSubject(), r.getDescription(), r.getStatus(), r.getCreatedAt()); }
    }
}
