package com.codecollab.pullrequest;

import java.util.List;

public record CreatePRRequest(
        String title,
        String description,
        Long repoId,
        List<Long> reviewerIds,
        List<FileData> files
) {
    public record FileData(String filename, String baseContent, String changedContent) {}
}
