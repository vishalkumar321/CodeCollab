package com.codecollab.audit;

import com.codecollab.audit.model.AuditLog;
import com.codecollab.pullrequest.model.PullRequest;
import com.codecollab.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AuditLogService — DESIGN PATTERN: Command + Audit Log
 * Every PR state change is stored as an immutable command record.
 * NO update or delete operations are exposed — this is intentional.
 */
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public AuditLog createEntry(PullRequest pr, User actor, String fromState, String toState, String action) {
        AuditLog log = AuditLog.builder()
                .pullRequest(pr)
                .actor(actor)
                .fromState(fromState)
                .toState(toState)
                .action(action)
                .timestamp(LocalDateTime.now())
                .build();
        return auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getAuditTrail(Long prId) {
        return auditLogRepository.findByPullRequestIdOrderByTimestampAsc(prId);
    }
    // NOTE: No update() or delete() methods — audit logs are immutable by design
}
