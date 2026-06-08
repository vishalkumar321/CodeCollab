package com.codecollab.events;

import com.codecollab.pullrequest.model.PullRequest;
import com.codecollab.user.model.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * DESIGN PATTERN: Observer (via Spring ApplicationEvent)
 * Published whenever a PR transitions between states.
 * Listeners: AuditLogListener, SLATrackerListener, AutoReviewListener
 */
@Getter
public class PRStateChangeEvent extends ApplicationEvent {

    private final PullRequest pullRequest;
    private final PullRequest.PRStatus fromStatus;
    private final PullRequest.PRStatus toStatus;
    private final User actor;

    public PRStateChangeEvent(Object source, PullRequest pullRequest,
                               PullRequest.PRStatus fromStatus,
                               PullRequest.PRStatus toStatus,
                               User actor) {
        super(source);
        this.pullRequest = pullRequest;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.actor = actor;
    }
}
