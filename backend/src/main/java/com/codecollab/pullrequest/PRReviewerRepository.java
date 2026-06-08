package com.codecollab.pullrequest;

import com.codecollab.pullrequest.model.PRReviewer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PRReviewerRepository extends JpaRepository<PRReviewer, Long> {
    List<PRReviewer> findByPullRequestId(Long prId);
    void deleteByPullRequestId(Long prId);
    boolean existsByPullRequestIdAndReviewerId(Long prId, Long reviewerId);
}
