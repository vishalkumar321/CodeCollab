package com.codecollab.events;

import com.codecollab.audit.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * DESIGN PATTERN: Observer — Listener
 * Listens for PRStateChangeEvent and writes immutable audit log entry.
 */
@Component
@RequiredArgsConstructor
public class AuditLogListener {

    private final AuditLogService auditLogService;

    @EventListener
    public void handlePRStateChange(PRStateChangeEvent event) {
        String fromState = event.getFromStatus() != null ? event.getFromStatus().name() : "NONE";
        String toState = event.getToStatus().name();
        String action = String.format("STATE_TRANSITION: %s -> %s", fromState, toState);

        auditLogService.createEntry(
                event.getPullRequest(),
                event.getActor(),
                fromState,
                toState,
                action
        );
    }
}
