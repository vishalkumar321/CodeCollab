package com.codecollab.sla;

import com.codecollab.audit.AuditLogService;
import com.codecollab.events.PREventPublisher;
import com.codecollab.pullrequest.model.PullRequest;
import com.codecollab.sla.model.SLATracker;
import com.codecollab.user.UserRepository;
import com.codecollab.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SLA Scheduler — runs every hour to detect breached SLA deadlines.
 * If PR is IN_REVIEW past the deadline, marks as BREACHED and ESCALATED.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SLAScheduler {

    private final SLATrackerRepository slaTrackerRepository;
    private final AuditLogService auditLogService;
    private final PREventPublisher eventPublisher;
    private final UserRepository userRepository;

    @Scheduled(fixedRate = 3600000) // Every hour
    @Transactional
    public void checkSLABreaches() {
        log.info("Running SLA breach check at {}", LocalDateTime.now());

        List<SLATracker> breached = slaTrackerRepository.findBreachedUnescalated();
        log.info("Found {} SLA breaches", breached.size());

        for (SLATracker tracker : breached) {
            PullRequest pr = tracker.getPullRequest();
            tracker.setBreached(true);
            tracker.setEscalatedAt(LocalDateTime.now());
            slaTrackerRepository.save(tracker);

            // Find a system/admin user to act as the escalation actor
            User systemActor = userRepository.findByRole(User.Role.ADMIN)
                    .stream().findFirst()
                    .orElse(pr.getAuthor());

            auditLogService.createEntry(
                    pr,
                    systemActor,
                    PullRequest.PRStatus.IN_REVIEW.name(),
                    "ESCALATED",
                    "SLA_BREACH_ESCALATION"
            );

            eventPublisher.publishSLABreach(pr);
            log.warn("PR #{} SLA breached and escalated", pr.getId());
        }
    }
}
