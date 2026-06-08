package com.codecollab.autoreview.rules;

import com.codecollab.autoreview.AutoReviewHandler;
import com.codecollab.autoreview.model.AutoReviewResult;
import com.codecollab.pullrequest.model.PullRequest;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Rule 4 — EmptyDescriptionRule (Chain of Responsibility)
 * Flags if PR description is empty or less than 10 characters. Severity: INFO
 */
@Component
public class EmptyDescriptionRule extends AutoReviewHandler {

    private static final int MIN_DESCRIPTION_LENGTH = 10;

    @Override
    protected void applyRule(PullRequest pr, List<AutoReviewResult> results) {
        String description = pr.getDescription();
        if (description == null || description.trim().length() < MIN_DESCRIPTION_LENGTH) {
            results.add(buildResult(pr,
                    "EmptyDescriptionRule",
                    AutoReviewResult.Severity.INFO,
                    String.format("PR description is too short (%d chars). Please add a meaningful description (at least %d characters).",
                            description == null ? 0 : description.trim().length(), MIN_DESCRIPTION_LENGTH)
            ));
        }
    }
}
