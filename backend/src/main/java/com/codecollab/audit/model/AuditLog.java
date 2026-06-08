package com.codecollab.audit.model;

import com.codecollab.pullrequest.model.PullRequest;
import com.codecollab.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * AuditLog - Immutable record of every PR state transition.
 * DESIGN PATTERN: Command + Audit Log — each action is stored as an immutable command record.
 * No updates or deletes are allowed at the service layer (enforced in AuditLogService).
 */
@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pr_id", nullable = false)
    private PullRequest pullRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor;

    @Column(name = "from_state")
    private String fromState;

    @Column(name = "to_state")
    private String toState;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
