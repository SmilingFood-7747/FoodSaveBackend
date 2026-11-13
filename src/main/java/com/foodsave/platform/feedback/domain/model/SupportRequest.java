package com.foodsave.platform.feedback.domain.model;

import java.time.Instant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "support_requests")
public class SupportRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "requester_account_id", nullable = false) private Long requesterAccountId;
    @Column(name = "reservation_id") private Long reservationId;
    @Column(nullable = false, length = 200) private String subject;
    @Column(nullable = false, columnDefinition = "text") private String description;
    @Column(nullable = false, length = 20) private String status = "OPEN";
    @Column(name = "created_at", nullable = false) private Instant createdAt = Instant.now();
    protected SupportRequest() {}
    public SupportRequest(Long accountId, Long reservationId, String subject, String description) {
        this.requesterAccountId = accountId; this.reservationId = reservationId; this.subject = subject.trim(); this.description = description.trim(); this.createdAt = Instant.now();
    }
    public Long getId() { return id; } public Long getRequesterAccountId() { return requesterAccountId; }
    public Long getReservationId() { return reservationId; } public String getSubject() { return subject; }
    public String getDescription() { return description; } public String getStatus() { return status; } public Instant getCreatedAt() { return createdAt; }
}
