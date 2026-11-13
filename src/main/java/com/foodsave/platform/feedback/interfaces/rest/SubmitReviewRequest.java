package com.foodsave.platform.feedback.interfaces.rest;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SubmitReviewRequest(@NotNull Long reservationId, @Min(1) @Max(5) int rating, @Size(max = 2000) String comment) {}
