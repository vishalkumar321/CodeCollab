package com.codecollab.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Name cannot be blank")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        String name,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email must be a valid email address")
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
        String password,

        @NotBlank(message = "Role is required")
        @Pattern(regexp = "^(?i)(admin|developer|reviewer)$", message = "Role must be ADMIN, DEVELOPER, or REVIEWER")
        String role
) {}
