package com.foodsave.platform.offers.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

@Entity
@Table(name = "offers")
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "business_id", nullable = false)
    private Long businessId;
    @Column(nullable = false, length = 80)
    private String category;
    @Column(nullable = false, length = 255)
    private String title;
    @Column(nullable = false, columnDefinition = "text")
    private String description;
    @Column(nullable = false, columnDefinition = "text")
    private String allergens = "";
    @Column(name = "original_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal originalPrice;
    @Column(name = "offer_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal offerPrice;
    @Column(name = "initial_units", nullable = false)
    private int initialUnits;
    @Column(name = "available_units", nullable = false)
    private int availableUnits;
    @Column(name = "pickup_start_at", nullable = false)
    private Instant pickupStartAt;
    @Column(name = "pickup_end_at", nullable = false)
    private Instant pickupEndAt;
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OfferStatus status = OfferStatus.ACTIVE;
    @Column(name = "image_url", nullable = false, length = 2048)
    private String image = "";
    @Column(name = "plus_exclusive", nullable = false)
    private boolean plusExclusive;

    protected Offer() {}

    public Offer(Long businessId, String category, String title, String description,
                 String allergens, BigDecimal originalPrice, BigDecimal offerPrice,
                 int initialUnits, Instant pickupStartAt, Instant pickupEndAt,
                 Instant expiresAt, String image, boolean plusExclusive) {
        this.businessId = businessId;
        this.category = category;
        this.title = title;
        this.description = description;
        this.allergens = allergens == null ? "" : allergens;
        this.originalPrice = originalPrice;
        this.offerPrice = offerPrice;
        this.initialUnits = initialUnits;
        this.availableUnits = initialUnits;
        this.pickupStartAt = pickupStartAt;
        this.pickupEndAt = pickupEndAt;
        this.expiresAt = expiresAt;
        this.image = image == null ? "" : image;
        this.plusExclusive = plusExclusive;
        validate();
    }

    public void validate() {
        if (title == null || title.isBlank() || description == null || description.isBlank())
            throw new IllegalArgumentException("offer.title.description.required");
        if (category == null || category.isBlank())
            throw new IllegalArgumentException("offer.category.required");
        if (originalPrice == null || offerPrice == null || offerPrice.signum() <= 0 || originalPrice.compareTo(offerPrice) <= 0)
            throw new IllegalArgumentException("offer.price.invalid");
        if (initialUnits < 1 || availableUnits < 0 || availableUnits > initialUnits)
            throw new IllegalArgumentException("offer.units.invalid");
        if (pickupStartAt == null || pickupEndAt == null || expiresAt == null
                || !pickupStartAt.isBefore(pickupEndAt) || expiresAt.isAfter(pickupEndAt))
            throw new IllegalArgumentException("offer.pickup.window.invalid");
    }

    public void pause() { this.status = OfferStatus.PAUSED; }
    public void resume() {
        if (expiresAt.isBefore(Instant.now()) || availableUnits < 1) throw new IllegalArgumentException("offer.not.resumable");
        this.status = OfferStatus.ACTIVE;
    }
    public void expire() { if (expiresAt != null && !expiresAt.isAfter(Instant.now()) && status == OfferStatus.ACTIVE) status = OfferStatus.EXPIRED; }
    public void reserveUnits(int quantity) {
        if (quantity < 1 || status != OfferStatus.ACTIVE || expiresAt.isBefore(Instant.now()) || quantity > availableUnits)
            throw new IllegalArgumentException("offer.not.reservable");
        availableUnits -= quantity;
    }
    public void releaseUnits(int quantity) {
        if (quantity < 1 || availableUnits + quantity > initialUnits) throw new IllegalArgumentException("offer.units.invalid");
        availableUnits += quantity;
    }
    public void replaceWith(Offer replacement) {
        this.businessId = replacement.businessId;
        this.category = replacement.category;
        this.title = replacement.title;
        this.description = replacement.description;
        this.allergens = replacement.allergens;
        this.originalPrice = replacement.originalPrice;
        this.offerPrice = replacement.offerPrice;
        this.initialUnits = replacement.initialUnits;
        this.availableUnits = Math.min(this.availableUnits, replacement.initialUnits);
        this.pickupStartAt = replacement.pickupStartAt;
        this.pickupEndAt = replacement.pickupEndAt;
        this.expiresAt = replacement.expiresAt;
        this.status = replacement.status;
        this.image = replacement.image;
        this.plusExclusive = replacement.plusExclusive;
        validate();
    }
    public Long getId() { return id; }
    public Long getBusinessId() { return businessId; }
    public String getCategory() { return category; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getAllergens() { return allergens; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public BigDecimal getOfferPrice() { return offerPrice; }
    public int getInitialUnits() { return initialUnits; }
    public int getAvailableUnits() { return availableUnits; }
    public Instant getPickupStartAt() { return pickupStartAt; }
    public Instant getPickupEndAt() { return pickupEndAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public String getStatus() { return status.name(); }
    public String getImage() { return image; }
    public boolean isPlusExclusive() { return plusExclusive; }
}
