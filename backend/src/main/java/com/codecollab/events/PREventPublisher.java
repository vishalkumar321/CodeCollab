package com.codecollab.events;

import com.codecollab.pullrequest.model.PullRequest;
import com.codecollab.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * DESIGN PATTERN: Observer — Publisher
 * Publishes PR state change and SLA breach events to all registered listeners.
 */
@Component
@RequiredArgsConstructor
public class PREventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishStateChange(PullRequest pr, PullRequest.PRStatus from, PullRequest.PRStatus to, User actor) {
        publisher.publishEvent(new PRStateChangeEvent(this, pr, from, to, actor));
    }

    public void publishSLABreach(PullRequest pr) {
        publisher.publishEvent(new SLABreachEvent(this, pr));
    }
}
