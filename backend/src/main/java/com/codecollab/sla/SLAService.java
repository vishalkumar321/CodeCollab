package com.codecollab.sla;

import com.codecollab.pullrequest.model.PullRequest;
import com.codecollab.sla.model.SLATracker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SLAService {

    private final SLATrackerRepository slaTrackerRepository;

    @Value("${app.sla.deadline-hours:24}")
    private int deadlineHours;

    @Transactional
    public SLATracker createTracker(PullRequest pr) {
        // Only create if not already tracked
        Optional<SLATracker> existing = slaTrackerRepository.findByPullRequestId(pr.getId());
        if (existing.isPresent()) return existing.get();

        LocalDateTime now = LocalDateTime.now();
        SLATracker tracker = SLATracker.builder()
                .pullRequest(pr)
                .startedAt(now)
                .deadline(now.plusHours(deadlineHours))
                .breached(false)
                .build();
        return slaTrackerRepository.save(tracker);
    }

    @Transactional(readOnly = true)
    public Optional<SLATracker> getTrackerByPrId(Long prId) {
        return slaTrackerRepository.findByPullRequestId(prId);
    }

    @Transactional(readOnly = true)
    public List<SLATracker> getBreachedTrackers() {
        return slaTrackerRepository.findByBreachedTrue();
    }

    public String getSLAStatus(SLATracker tracker) {
        if (tracker == null) return "N/A";
        if (tracker.getEscalatedAt() != null) return "ESCALATED";
        if (tracker.getBreached()) return "BREACHED";
        return "ON_TRACK";
    }

    @Transactional
    public void markBreached(SLATracker tracker) {
        tracker.setBreached(true);
        tracker.setEscalatedAt(LocalDateTime.now());
        slaTrackerRepository.save(tracker);
    }
}
