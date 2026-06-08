package com.codecollab.events;

import com.codecollab.pullrequest.model.PullRequest;
import com.codecollab.sla.SLAService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * DESIGN PATTERN: Observer — Listener
 * Listens for PRStateChangeEvent and creates SLA tracker when PR enters IN_REVIEW.
 */
@Component
@RequiredArgsConstructor
public class SLATrackerListener {

    private final SLAService slaService;

    @EventListener
    public void handlePRStateChange(PRStateChangeEvent event) {
        if (event.getToStatus() == PullRequest.PRStatus.IN_REVIEW) {
            slaService.createTracker(event.getPullRequest());
        }
    }
}
