package com.codecollab.autoreview;

import com.codecollab.autoreview.model.AutoReviewResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutoReviewResultRepository extends JpaRepository<AutoReviewResult, Long> {
    List<AutoReviewResult> findByPullRequestId(Long prId);
    void deleteByPullRequestId(Long prId);
}
