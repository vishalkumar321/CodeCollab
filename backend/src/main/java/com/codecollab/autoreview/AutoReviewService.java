package com.codecollab.autoreview;

import com.codecollab.autoreview.model.AutoReviewResult;
import com.codecollab.autoreview.rules.*;
import com.codecollab.pullrequest.model.PullRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * AutoReviewService — builds and runs the Chain of Responsibility pipeline.
 * Chain order: FileSizeRule → FileCountRule → BannedKeywordRule → EmptyDescriptionRule
 */
@Service
@RequiredArgsConstructor
public class AutoReviewService {

    private final FileSizeRule fileSizeRule;
    private final FileCountRule fileCountRule;
    private final BannedKeywordRule bannedKeywordRule;
    private final EmptyDescriptionRule emptyDescriptionRule;
    private final AutoReviewResultRepository resultRepository;

    @Transactional
    public List<AutoReviewResult> runAutoReview(PullRequest pr) {
        // Build the chain: FileSizeRule → FileCountRule → BannedKeywordRule → EmptyDescriptionRule
        fileSizeRule.setNext(fileCountRule)
                    .setNext(bannedKeywordRule)
                    .setNext(emptyDescriptionRule);

        List<AutoReviewResult> results = new ArrayList<>();
        fileSizeRule.handle(pr, results);

        // Clear previous results and save new ones
        resultRepository.deleteByPullRequestId(pr.getId());
        return resultRepository.saveAll(results);
    }

    @Transactional(readOnly = true)
    public List<AutoReviewResult> getResults(Long prId) {
        return resultRepository.findByPullRequestId(prId);
    }
}
