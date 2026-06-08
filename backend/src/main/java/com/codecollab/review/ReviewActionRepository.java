package com.codecollab.review;

import com.codecollab.review.model.ReviewAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewActionRepository extends JpaRepository<ReviewAction, Long> {
    List<ReviewAction> findByPullRequestId(Long prId);
    List<ReviewAction> findByPullRequestIdAndReviewerId(Long prId, Long reviewerId);
    boolean existsByPullRequestIdAndAction(Long prId, ReviewAction.ReviewActionType action);
}
