package com.foodsave.platform.iam.interfaces.rest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.foodsave.platform.iam.domain.model.AccountRole;

public record SignUpRequest(@NotBlank @Size(max = 160) String fullName,
                            @NotBlank @Email @Size(max = 254) String email,
                            @Size(max = 30) String phone,
                            AccountRole role,
                            @NotBlank @Size(min = 8, max = 100) String password) {}
