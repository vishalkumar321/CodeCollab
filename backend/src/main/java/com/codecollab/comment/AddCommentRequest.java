package com.codecollab.comment;

public record AddCommentRequest(
        Long fileId,
        Integer lineNumber,
        String content,
        Long parentCommentId
) {}
