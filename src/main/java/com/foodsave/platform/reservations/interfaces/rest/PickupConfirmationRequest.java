package com.foodsave.platform.reservations.interfaces.rest;

import jakarta.validation.constraints.NotBlank;

public record PickupConfirmationRequest(@NotBlank String code) {}
