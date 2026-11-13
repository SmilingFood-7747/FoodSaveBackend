package com.foodsave.platform.notifications.interfaces.rest;

import java.math.BigDecimal;
import java.time.Instant;
import com.foodsave.platform.notifications.domain.model.Notification;

public record NotificationResource(Long id, Long recipientAccountId, Long reservationId, Long offerId, BigDecimal distanceKm,
                                   String offerVersion, String type, String titleKey, String body, Instant createdAt, Instant readAt) {
    public static NotificationResource from(Notification n) { return new NotificationResource(n.getId(), n.getRecipientAccountId(), n.getReservationId(), n.getOfferId(), n.getDistanceKm(), n.getOfferVersion(), n.getType(), n.getTitleKey(), n.getBody(), n.getCreatedAt(), n.getReadAt()); }
}
