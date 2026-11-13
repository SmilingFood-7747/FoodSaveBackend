package com.foodsave.platform.feedback.domain.model;

import java.time.Instant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reviews")
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "reservation_id", nullable = false, unique = true) private Long reservationId;
    @Column(nullable = false) private int rating;
    @Column(nullable = false, columnDefinition = "text") private String comment = "";
    @Column(name = "created_at", nullable = false) private Instant createdAt = Instant.now();
    protected Review() {}
    public Review(Long reservationId, int rating, String comment) {
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("review.rating.invalid");
        this.reservationId = reservationId; this.rating = rating; this.comment = comment == null ? "" : comment.trim(); this.createdAt = Instant.now();
    }
    public Long getId() { return id; } public Long getReservationId() { return reservationId; }
    public int getRating() { return rating; } public String getComment() { return comment; } public Instant getCreatedAt() { return createdAt; }
}
