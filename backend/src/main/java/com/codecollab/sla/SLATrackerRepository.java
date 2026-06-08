package com.codecollab.sla;

import com.codecollab.sla.model.SLATracker;
import com.codecollab.pullrequest.model.PullRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SLATrackerRepository extends JpaRepository<SLATracker, Long> {
    Optional<SLATracker> findByPullRequest(PullRequest pullRequest);
    Optional<SLATracker> findByPullRequestId(Long prId);

    @Query("SELECT s FROM SLATracker s WHERE s.pullRequest.status = 'IN_REVIEW' AND s.breached = false AND s.deadline < CURRENT_TIMESTAMP")
    List<SLATracker> findBreachedUnescalated();

    List<SLATracker> findByBreachedTrue();
}
