package com.codecollab.pullrequest;

import com.codecollab.pullrequest.model.PullRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PRRepository extends JpaRepository<PullRequest, Long> {

    List<PullRequest> findByRepositoryId(Long repoId);

    List<PullRequest> findByAuthorId(Long authorId);

    @Query("SELECT p FROM PullRequest p JOIN p.reviewers r WHERE r.reviewer.id = :reviewerId")
    List<PullRequest> findByReviewerId(@Param("reviewerId") Long reviewerId);

    List<PullRequest> findByStatus(PullRequest.PRStatus status);

    @Query("SELECT p FROM PullRequest p WHERE p.repository.id = :repoId AND p.status IN ('OPEN', 'IN_REVIEW', 'CHANGES_REQUESTED', 'APPROVED')")
    List<PullRequest> findOpenPRsByRepo(@Param("repoId") Long repoId);

    @Query("SELECT p FROM PullRequest p JOIN p.reviewers r WHERE r.reviewer.id = :reviewerId AND p.status = 'IN_REVIEW'")
    List<PullRequest> findInReviewByReviewer(@Param("reviewerId") Long reviewerId);
}
