package com.foodsave.platform.offers.interfaces.rest;

import java.math.BigDecimal;
import java.time.Instant;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OfferRequest(
        @NotNull @Positive Long businessId,
        @NotBlank String category,
        @NotBlank String title,
        @NotBlank String description,
        String allergens,
        @NotNull @DecimalMin("0.01") BigDecimal originalPrice,
        @NotNull @DecimalMin("0.01") BigDecimal offerPrice,
        @Positive int initialUnits,
        @NotNull Instant pickupStartAt,
        @NotNull Instant pickupEndAt,
        @NotNull Instant expiresAt,
        String image,
        boolean plusExclusive) {
}
