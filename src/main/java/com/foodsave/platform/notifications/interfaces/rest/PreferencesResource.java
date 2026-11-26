package com.foodsave.platform.notifications.interfaces.rest;

import com.foodsave.platform.notifications.domain.model.NotificationPreferences;
import java.util.Set;

public record PreferencesResource(Long accountId, boolean emailEnabled, boolean pushEnabled, boolean remindersEnabled, boolean nearbyOffersEnabled, Set<String> preferredCategories) {
    public static PreferencesResource from(NotificationPreferences p) { return new PreferencesResource(p.getAccountId(), p.isEmailEnabled(), p.isPushEnabled(), p.isRemindersEnabled(), p.isNearbyOffersEnabled(), p.getPreferredCategories()); }
}
