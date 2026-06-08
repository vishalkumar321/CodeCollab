package com.codecollab.events;

import com.codecollab.pullrequest.model.PullRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * DESIGN PATTERN: Observer
 * Published when SLA deadline is breached.
 */
@Getter
public class SLABreachEvent extends ApplicationEvent {

    private final PullRequest pullRequest;

    public SLABreachEvent(Object source, PullRequest pullRequest) {
        super(source);
        this.pullRequest = pullRequest;
    }
}
