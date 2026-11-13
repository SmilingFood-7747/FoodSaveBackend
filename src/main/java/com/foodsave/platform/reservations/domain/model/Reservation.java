package com.foodsave.platform.reservations.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "offer_id", nullable = false)
    private Long offerId;
    @Column(name = "business_id", nullable = false)
    private Long businessId;
    @Column(name = "customer_user_id", nullable = false)
    private Long customerUserId;
    @Column(nullable = false)
    private int quantity;
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
    @Column(name = "original_unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal originalUnitPrice;
    @Column(name = "pickup_code", nullable = false, unique = true, length = 32)
    private String pickupCode;
    @Column(name = "pickup_start_at", nullable = false)
    private Instant pickupStartAt;
    @Column(name = "pickup_deadline_at", nullable = false)
    private Instant pickupDeadlineAt;
    @Column(nullable = false, length = 30)
    private String status;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "collected_at")
    private Instant collectedAt;

    protected Reservation() {}

    public Reservation(Long offerId, Long businessId, Long customerUserId, int quantity,
                       BigDecimal unitPrice, BigDecimal originalUnitPrice, String pickupCode,
                       Instant pickupStartAt, Instant pickupDeadlineAt) {
        this.offerId = offerId;
        this.businessId = businessId;
        this.customerUserId = customerUserId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.originalUnitPrice = originalUnitPrice;
        this.pickupCode = pickupCode;
        this.pickupStartAt = pickupStartAt;
        this.pickupDeadlineAt = pickupDeadlineAt;
        this.status = "ACTIVE";
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getOfferId() { return offerId; }
    public Long getBusinessId() { return businessId; }
    public Long getCustomerUserId() { return customerUserId; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getOriginalUnitPrice() { return originalUnitPrice; }
    public String getPickupCode() { return pickupCode; }
    public Instant getPickupStartAt() { return pickupStartAt; }
    public Instant getPickupDeadlineAt() { return pickupDeadlineAt; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getCollectedAt() { return collectedAt; }
    public void confirmPickup(String code) {
        if (!pickupCode.equalsIgnoreCase(code == null ? "" : code.trim())) throw new IllegalArgumentException("reservation.code.invalid");
        if (!"ACTIVE".equals(status) || pickupDeadlineAt.isBefore(Instant.now())) throw new IllegalArgumentException("reservation.not.confirmable");
        status = "COLLECTED";
        collectedAt = Instant.now();
    }
    public void cancel(Long actorAccountId) {
        if (!customerUserId.equals(actorAccountId)) throw new IllegalArgumentException("reservation.actor.invalid");
        if (!"ACTIVE".equals(status) || pickupStartAt.isBefore(Instant.now())) throw new IllegalArgumentException("reservation.not.cancellable");
        status = "CANCELLED";
    }
}
