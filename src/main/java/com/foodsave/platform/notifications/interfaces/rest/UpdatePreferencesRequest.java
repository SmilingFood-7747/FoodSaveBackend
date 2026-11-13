package com.foodsave.platform.notifications.interfaces.rest;

public record UpdatePreferencesRequest(boolean emailEnabled, boolean pushEnabled, boolean remindersEnabled, boolean nearbyOffersEnabled) {}
