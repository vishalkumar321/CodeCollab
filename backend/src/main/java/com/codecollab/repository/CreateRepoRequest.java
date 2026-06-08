package com.codecollab.repository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRepoRequest(
        @NotBlank(message = "Repository name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        String name,

        @NotBlank(message = "Description is required")
        String description,

        @NotBlank(message = "Language is required")
        String language
) {}
