package com.codecollab.review;

import com.codecollab.pullrequest.PRRepository;
import com.codecollab.pullrequest.model.PullRequest;
import com.codecollab.review.model.ReviewAction;
import com.codecollab.user.model.User;

/**
 * DESIGN PATTERN: Template Method
 * Abstract base class defining the skeleton of the review submission process.
 * Subclasses override specific steps without changing the overall algorithm.
 */
public abstract class ReviewProcessTemplate {

    /**
     * Template method — defines the review processing algorithm.
     * Steps: preValidate → processAction → postProcess
     */
    public final ReviewAction submitReview(PullRequest pr, User reviewer,
                                            ReviewAction.ReviewActionType action) {
        preValidate(pr, reviewer, action);
        ReviewAction result = processAction(pr, reviewer, action);
        postProcess(pr, reviewer, action);
        return result;
    }

    /** Step 1: Validate before processing */
    protected abstract void preValidate(PullRequest pr, User reviewer,
                                         ReviewAction.ReviewActionType action);

    /** Step 2: Persist the review action */
    protected abstract ReviewAction processAction(PullRequest pr, User reviewer,
                                                   ReviewAction.ReviewActionType action);

    /** Step 3: Post-processing (state transitions, notifications) */
    protected abstract void postProcess(PullRequest pr, User reviewer,
                                         ReviewAction.ReviewActionType action);
}
