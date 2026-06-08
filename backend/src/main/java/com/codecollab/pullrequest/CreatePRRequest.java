package com.codecollab.pullrequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreatePRRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 100, message = "Title must not exceed 100 characters")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Repository ID is required")
        Long repoId,

        List<Long> reviewerIds,
        List<FileData> files
) {
    public record FileData(String filename, String baseContent, String changedContent) {}
}
