package com.foodsave.platform.notifications.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "recipient_account_id", nullable = false) private Long recipientAccountId;
    @Column(name = "reservation_id") private Long reservationId;
    @Column(name = "offer_id") private Long offerId;
    @Column(name = "distance_km", precision = 8, scale = 2) private BigDecimal distanceKm;
    @Column(name = "offer_version", length = 80) private String offerVersion;
    @Column(nullable = false, length = 40) private String type;
    @Column(name = "title_key", nullable = false, length = 120) private String titleKey;
    @Column(nullable = false, columnDefinition = "text") private String body;
    @Column(name = "created_at", nullable = false) private Instant createdAt = Instant.now();
    @Column(name = "read_at") private Instant readAt;

    protected Notification() {}
    public Notification(Long recipientAccountId, Long reservationId, Long offerId, BigDecimal distanceKm, String offerVersion, String type, String titleKey, String body) {
        this.recipientAccountId = recipientAccountId; this.reservationId = reservationId; this.offerId = offerId;
        this.distanceKm = distanceKm; this.offerVersion = offerVersion; this.type = type; this.titleKey = titleKey; this.body = body;
        this.createdAt = Instant.now();
    }
    public void markRead() { if (readAt == null) readAt = Instant.now(); }
    public Long getId() { return id; } public Long getRecipientAccountId() { return recipientAccountId; }
    public Long getReservationId() { return reservationId; } public Long getOfferId() { return offerId; }
    public BigDecimal getDistanceKm() { return distanceKm; } public String getOfferVersion() { return offerVersion; }
    public String getType() { return type; } public String getTitleKey() { return titleKey; }
    public String getBody() { return body; } public Instant getCreatedAt() { return createdAt; } public Instant getReadAt() { return readAt; }
}
