package com.foodsave.platform.iam.interfaces.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(@NotBlank @Size(max = 160) String fullName, @Size(max = 30) String phone) {}
