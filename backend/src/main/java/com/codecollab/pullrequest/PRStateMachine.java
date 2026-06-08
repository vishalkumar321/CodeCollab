package com.codecollab.pullrequest;

import com.codecollab.pullrequest.model.PullRequest;
import com.codecollab.pullrequest.model.PullRequest.PRStatus;
import com.codecollab.user.model.User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * DESIGN PATTERN: State Machine
 * Enforces valid PR lifecycle transitions per user role.
 *
 * Valid transitions:
 *   Author:   DRAFT→OPEN, OPEN→CLOSED, CHANGES_REQUESTED→OPEN
 *   Reviewer: OPEN→IN_REVIEW, IN_REVIEW→APPROVED, IN_REVIEW→CHANGES_REQUESTED
 *   Admin:    any→CLOSED, any→ESCALATED (handled separately)
 */
@Component
public class PRStateMachine {

    // Author-allowed transitions
    private static final Map<PRStatus, Set<PRStatus>> AUTHOR_TRANSITIONS = Map.of(
            PRStatus.DRAFT, Set.of(PRStatus.OPEN),
            PRStatus.OPEN, Set.of(PRStatus.CLOSED),
            PRStatus.CHANGES_REQUESTED, Set.of(PRStatus.OPEN)
    );

    // Reviewer-allowed transitions
    private static final Map<PRStatus, Set<PRStatus>> REVIEWER_TRANSITIONS = Map.of(
            PRStatus.OPEN, Set.of(PRStatus.IN_REVIEW),
            PRStatus.IN_REVIEW, Set.of(PRStatus.APPROVED, PRStatus.CHANGES_REQUESTED)
    );

    public void validateTransition(PRStatus from, PRStatus to, User.Role role) {
        boolean allowed = switch (role) {
            case AUTHOR -> isTransitionAllowed(AUTHOR_TRANSITIONS, from, to);
            case REVIEWER -> isTransitionAllowed(REVIEWER_TRANSITIONS, from, to);
            case ADMIN -> true; // Admin can force any transition
        };

        if (!allowed) {
            throw new InvalidStateTransitionException(
                    String.format("Role %s cannot transition PR from %s to %s", role, from, to)
            );
        }
    }

    private boolean isTransitionAllowed(Map<PRStatus, Set<PRStatus>> transitions, PRStatus from, PRStatus to) {
        return transitions.getOrDefault(from, Set.of()).contains(to);
    }

    public static class InvalidStateTransitionException extends RuntimeException {
        public InvalidStateTransitionException(String message) {
            super(message);
        }
    }
}
