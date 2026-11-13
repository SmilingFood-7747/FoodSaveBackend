package com.foodsave.platform.reservations.interfaces.rest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReservationRequest(@NotNull @Positive Long offerId,
                                 Long customerUserId,
                                 @Positive int quantity) {
}
