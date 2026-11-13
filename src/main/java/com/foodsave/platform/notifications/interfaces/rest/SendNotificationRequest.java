package com.foodsave.platform.notifications.interfaces.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SendNotificationRequest(@NotNull Long recipientAccountId, Long reservationId, Long offerId,
                                      @NotBlank @Size(max = 40) String type,
                                      @NotBlank @Size(max = 120) String titleKey,
                                      @NotBlank @Size(max = 2000) String body) {}
