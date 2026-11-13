package com.foodsave.platform.feedback.interfaces.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OpenSupportRequest(Long reservationId, @NotBlank @Size(max = 200) String subject, @NotBlank @Size(max = 4000) String description) {}
