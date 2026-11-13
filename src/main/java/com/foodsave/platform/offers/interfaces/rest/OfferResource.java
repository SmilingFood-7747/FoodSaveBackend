package com.foodsave.platform.offers.interfaces.rest;

import java.math.BigDecimal;
import java.time.Instant;
import com.foodsave.platform.offers.domain.model.Offer;

public record OfferResource(Long id, Long businessId, String category, String title, String description,
                            String allergens, BigDecimal originalPrice, BigDecimal offerPrice,
                            int initialUnits, int availableUnits, Instant pickupStartAt,
                            Instant pickupEndAt, Instant expiresAt, String status,
                            String image, boolean plusExclusive) {
    public static OfferResource from(Offer offer) {
        return new OfferResource(offer.getId(), offer.getBusinessId(), offer.getCategory(), offer.getTitle(),
                offer.getDescription(), offer.getAllergens(), offer.getOriginalPrice(), offer.getOfferPrice(),
                offer.getInitialUnits(), offer.getAvailableUnits(), offer.getPickupStartAt(), offer.getPickupEndAt(),
                offer.getExpiresAt(), offer.getStatus(), offer.getImage(), offer.isPlusExclusive());
    }
}
