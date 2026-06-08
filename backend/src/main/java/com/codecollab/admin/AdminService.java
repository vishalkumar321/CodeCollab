package com.codecollab.admin;

import com.codecollab.audit.AuditLogService;
import com.codecollab.audit.model.AuditLog;
import com.codecollab.pullrequest.PRRepository;
import com.codecollab.pullrequest.PRReviewerRepository;
import com.codecollab.pullrequest.model.PRReviewer;
import com.codecollab.pullrequest.model.PullRequest;
import com.codecollab.pullrequest.model.PullRequest.PRStatus;
import com.codecollab.events.PREventPublisher;
import com.codecollab.sla.SLAService;
import com.codecollab.sla.SLATrackerRepository;
import com.codecollab.sla.model.SLATracker;
import com.codecollab.user.UserRepository;
import com.codecollab.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final PRRepository prRepository;
    private final PRReviewerRepository prReviewerRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final SLAService slaService;
    private final SLATrackerRepository slaTrackerRepository;
    private final PREventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<PullRequest> getAllPRs(String status, Long repoId, Long reviewerId) {
        if (status != null) return prRepository.findByStatus(PRStatus.valueOf(status.toUpperCase()));
        if (repoId != null) return prRepository.findByRepositoryId(repoId);
        if (reviewerId != null) return prRepository.findByReviewerId(reviewerId);
        return prRepository.findAll();
    }

    @Transactional
    public void reassignReviewers(Long prId, List<Long> reviewerIds, Long adminId) {
        PullRequest pr = prRepository.findById(prId)
                .orElseThrow(() -> new IllegalArgumentException("PR not found"));

        // Remove old reviewers
        prReviewerRepository.deleteByPullRequestId(prId);

        // Add new reviewers (cannot assign PR author)
        for (Long reviewerId : reviewerIds) {
            if (reviewerId.equals(pr.getAuthor().getId())) continue;
            User reviewer = userRepository.findById(reviewerId).orElse(null);
            if (reviewer != null) {
                PRReviewer prReviewer = PRReviewer.builder()
                        .pullRequest(pr)
                        .reviewer(reviewer)
                        .build();
                prReviewerRepository.save(prReviewer);
            }
        }

        User admin = userRepository.findById(adminId).orElseThrow();
        auditLogService.createEntry(pr, admin, pr.getStatus().name(), pr.getStatus().name(), "REVIEWER_REASSIGNMENT");
    }

    @Transactional
    public PullRequest forcePRStatus(Long prId, PRStatus newStatus, Long adminId) {
        PullRequest pr = prRepository.findById(prId)
                .orElseThrow(() -> new IllegalArgumentException("PR not found"));
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        PRStatus oldStatus = pr.getStatus();
        pr.setStatus(newStatus);
        PullRequest saved = prRepository.save(pr);
        eventPublisher.publishStateChange(saved, oldStatus, newStatus, admin);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<SLATracker> getBreachedSLAs() {
        return slaService.getBreachedTrackers();
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getReviewerWorkload() {
        List<User> reviewers = userRepository.findByRole(User.Role.REVIEWER);
        return reviewers.stream().collect(Collectors.toMap(
                User::getName,
                reviewer -> (long) prRepository.findInReviewByReviewer(reviewer.getId()).size()
        ));
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getAuditTrail(Long prId) {
        return auditLogService.getAuditTrail(prId);
    }
}
