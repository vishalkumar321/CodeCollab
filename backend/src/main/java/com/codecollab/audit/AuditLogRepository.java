package com.codecollab.audit;

import com.codecollab.audit.model.AuditLog;
import com.codecollab.pullrequest.model.PullRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AuditLog repository — READ and CREATE only.
 * No update/delete methods exposed (immutability enforced at service layer).
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByPullRequestOrderByTimestampAsc(PullRequest pullRequest);
    List<AuditLog> findByPullRequestIdOrderByTimestampAsc(Long prId);
}
