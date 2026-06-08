package com.codecollab.autoreview.rules;

import com.codecollab.autoreview.AutoReviewHandler;
import com.codecollab.autoreview.model.AutoReviewResult;
import com.codecollab.pullrequest.model.PRFile;
import com.codecollab.pullrequest.model.PullRequest;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Rule 1 — FileSizeRule (Chain of Responsibility)
 * Flags any file with changed_content > 300 lines as WARNING.
 */
@Component
public class FileSizeRule extends AutoReviewHandler {

    private static final int MAX_LINES = 300;

    @Override
    protected void applyRule(PullRequest pr, List<AutoReviewResult> results) {
        for (PRFile file : pr.getFiles()) {
            if (file.getChangedContent() != null) {
                long lineCount = file.getChangedContent().lines().count();
                if (lineCount > MAX_LINES) {
                    results.add(buildResult(pr,
                            "FileSizeRule",
                            AutoReviewResult.Severity.WARNING,
                            String.format("File '%s' has %d lines, which exceeds the %d line limit.",
                                    file.getFilename(), lineCount, MAX_LINES)
                    ));
                }
            }
        }
    }
}
