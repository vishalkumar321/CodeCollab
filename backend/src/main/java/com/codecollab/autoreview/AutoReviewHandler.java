package com.codecollab.autoreview;

import com.codecollab.autoreview.model.AutoReviewResult;
import com.codecollab.pullrequest.model.PullRequest;

import java.util.List;

/**
 * DESIGN PATTERN: Chain of Responsibility
 * Abstract handler for the auto-review rule pipeline.
 * Each rule processes the PR and passes to the next handler in the chain.
 */
public abstract class AutoReviewHandler {

    protected AutoReviewHandler next;

    public AutoReviewHandler setNext(AutoReviewHandler next) {
        this.next = next;
        return next;
    }

    /**
     * Process this rule and pass to next handler.
     */
    public void handle(PullRequest pr, List<AutoReviewResult> results) {
        applyRule(pr, results);
        if (next != null) {
            next.handle(pr, results);
        }
    }

    protected abstract void applyRule(PullRequest pr, List<AutoReviewResult> results);

    protected AutoReviewResult buildResult(PullRequest pr, String ruleName,
                                           AutoReviewResult.Severity severity, String message) {
        return AutoReviewResult.builder()
                .pullRequest(pr)
                .ruleName(ruleName)
                .severity(severity)
                .message(message)
                .build();
    }
}
