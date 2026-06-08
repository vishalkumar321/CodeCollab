package com.codecollab.autoreview.rules;

import com.codecollab.autoreview.AutoReviewHandler;
import com.codecollab.autoreview.model.AutoReviewResult;
import com.codecollab.pullrequest.model.PullRequest;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Rule 2 — FileCountRule (Chain of Responsibility)
 * Flags if PR has more than 10 files as WARNING.
 */
@Component
public class FileCountRule extends AutoReviewHandler {

    private static final int MAX_FILES = 10;

    @Override
    protected void applyRule(PullRequest pr, List<AutoReviewResult> results) {
        int fileCount = pr.getFiles().size();
        if (fileCount > MAX_FILES) {
            results.add(buildResult(pr,
                    "FileCountRule",
                    AutoReviewResult.Severity.WARNING,
                    String.format("PR contains %d files, which exceeds the recommended limit of %d files per PR.",
                            fileCount, MAX_FILES)
            ));
        }
    }
}
