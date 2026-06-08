package com.codecollab.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddCommentRequest(
        @NotNull(message = "File ID is required")
        Long fileId,

        Integer lineNumber,

        @NotBlank(message = "Comment content cannot be empty")
        String content,

        Long parentCommentId
) {}
