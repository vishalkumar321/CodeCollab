package com.codecollab.sla.model;

import com.codecollab.pullrequest.model.PullRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sla_trackers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SLATracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pr_id", nullable = false, unique = true)
    private PullRequest pullRequest;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Column(nullable = false)
    @Builder.Default
    private Boolean breached = false;

    @Column(name = "escalated_at")
    private LocalDateTime escalatedAt;
}
