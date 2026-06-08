package com.codecollab.review;

import com.codecollab.events.PREventPublisher;
import com.codecollab.pullrequest.PRRepository;
import com.codecollab.pullrequest.model.PullRequest;
import com.codecollab.pullrequest.model.PullRequest.PRStatus;
import com.codecollab.review.model.ReviewAction;
import com.codecollab.review.model.ReviewAction.ReviewActionType;
import com.codecollab.user.UserRepository;
import com.codecollab.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ReviewService extends ReviewProcessTemplate (Template Method pattern).
 * Implements the three steps of the review submission algorithm.
 */
@Service
@RequiredArgsConstructor
public class ReviewService extends ReviewProcessTemplate {

    private final ReviewActionRepository reviewActionRepository;
    private final PRRepository prRepository;
    private final UserRepository userRepository;
    private final PREventPublisher eventPublisher;

    private PRRepository getPRRepository() { return prRepository; }

    @Override
    protected void preValidate(PullRequest pr, User reviewer, ReviewActionType action) {
        if (pr.getStatus() != PRStatus.IN_REVIEW) {
            throw new IllegalStateException("PR must be IN_REVIEW to submit a review");
        }
    }

    @Override
    protected ReviewAction processAction(PullRequest pr, User reviewer, ReviewActionType action) {
        ReviewAction reviewAction = ReviewAction.builder()
                .pullRequest(pr)
                .reviewer(reviewer)
                .action(action)
                .build();
        return reviewActionRepository.save(reviewAction);
    }

    @Override
    protected void postProcess(PullRequest pr, User reviewer, ReviewActionType action) {
        if (action == ReviewActionType.APPROVE) {
            // Approve only if no open REQUEST_CHANGES exist
            boolean hasOpenRequestChanges = reviewActionRepository
                    .existsByPullRequestIdAndAction(pr.getId(), ReviewActionType.REQUEST_CHANGES);

            if (!hasOpenRequestChanges) {
                PRStatus oldStatus = pr.getStatus();
                pr.setStatus(PRStatus.APPROVED);
                PullRequest saved = prRepository.save(pr);
                eventPublisher.publishStateChange(saved, oldStatus, PRStatus.APPROVED, reviewer);
            }
        } else if (action == ReviewActionType.REQUEST_CHANGES) {
            PRStatus oldStatus = pr.getStatus();
            pr.setStatus(PRStatus.CHANGES_REQUESTED);
            PullRequest saved = prRepository.save(pr);
            eventPublisher.publishStateChange(saved, oldStatus, PRStatus.CHANGES_REQUESTED, reviewer);
        }
        // COMMENT action does not change PR status
    }

    @Transactional
    public ReviewAction submitReviewAction(Long prId, Long reviewerId, ReviewActionType action) {
        PullRequest pr = prRepository.findById(prId)
                .orElseThrow(() -> new IllegalArgumentException("PR not found"));
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new IllegalArgumentException("Reviewer not found"));
        return submitReview(pr, reviewer, action);
    }

    @Transactional(readOnly = true)
    public List<ReviewAction> getReviewActions(Long prId) {
        return reviewActionRepository.findByPullRequestId(prId);
    }
}
