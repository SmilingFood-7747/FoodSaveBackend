package com.foodsave.platform.reservations.interfaces.rest;

import java.math.BigDecimal;
import java.time.Instant;
import com.foodsave.platform.reservations.domain.model.Reservation;

public record ReservationResource(Long id, Long offerId, Long businessId, Long customerUserId,
                                  int quantity, BigDecimal unitPrice, BigDecimal originalUnitPrice,
                                  String pickupCode, Instant pickupStartAt, Instant pickupDeadlineAt,
                                  String status, Instant createdAt, Instant collectedAt) {
    public static ReservationResource from(Reservation reservation) {
        return new ReservationResource(reservation.getId(), reservation.getOfferId(), reservation.getBusinessId(),
                reservation.getCustomerUserId(), reservation.getQuantity(), reservation.getUnitPrice(),
                reservation.getOriginalUnitPrice(), reservation.getPickupCode(), reservation.getPickupStartAt(),
                reservation.getPickupDeadlineAt(), reservation.getStatus(), reservation.getCreatedAt(), reservation.getCollectedAt());
    }
}
