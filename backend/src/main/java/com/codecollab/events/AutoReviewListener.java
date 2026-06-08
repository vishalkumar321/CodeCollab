package com.codecollab.events;

import com.codecollab.autoreview.AutoReviewService;
import com.codecollab.pullrequest.PRRepository;
import com.codecollab.pullrequest.model.PullRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * DESIGN PATTERN: Observer — Listener
 * Triggers auto-review chain when PR transitions to OPEN state.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AutoReviewListener {

    private final AutoReviewService autoReviewService;
    private final PRRepository prRepository;

    @EventListener
    public void handlePRStateChange(PRStateChangeEvent event) {
        if (event.getToStatus() == PullRequest.PRStatus.OPEN) {
            // Reload PR with files eagerly to avoid lazy loading issues
            prRepository.findById(event.getPullRequest().getId()).ifPresent(pr -> {
                log.info("Triggering auto-review for PR #{}", pr.getId());
                autoReviewService.runAutoReview(pr);
            });
        }
    }
}
