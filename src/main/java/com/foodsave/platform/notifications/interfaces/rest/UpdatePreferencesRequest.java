package com.foodsave.platform.notifications.interfaces.rest;

import java.util.Set;

public record UpdatePreferencesRequest(boolean emailEnabled, boolean pushEnabled, boolean remindersEnabled, boolean nearbyOffersEnabled, Set<String> preferredCategories) {
    public UpdatePreferencesRequest {
        preferredCategories = preferredCategories == null ? Set.of() : Set.copyOf(preferredCategories);
    }
}
