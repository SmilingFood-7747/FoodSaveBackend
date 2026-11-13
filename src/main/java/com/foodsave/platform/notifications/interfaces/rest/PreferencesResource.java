package com.foodsave.platform.notifications.interfaces.rest;

import com.foodsave.platform.notifications.domain.model.NotificationPreferences;

public record PreferencesResource(Long accountId, boolean emailEnabled, boolean pushEnabled, boolean remindersEnabled, boolean nearbyOffersEnabled) {
    public static PreferencesResource from(NotificationPreferences p) { return new PreferencesResource(p.getAccountId(), p.isEmailEnabled(), p.isPushEnabled(), p.isRemindersEnabled(), p.isNearbyOffersEnabled()); }
}
