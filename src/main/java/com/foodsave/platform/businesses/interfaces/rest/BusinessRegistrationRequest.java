package com.foodsave.platform.businesses.interfaces.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record BusinessRegistrationRequest(@NotBlank @Size(max = 255) String name,
                                          @NotBlank @Size(max = 255) String address,
                                          @NotBlank @Size(max = 100) String district,
                                          @NotNull Double latitude, @NotNull Double longitude,
                                          @Size(max = 30) String contactPhone,
                                          @NotBlank String pickupConditions) {}
