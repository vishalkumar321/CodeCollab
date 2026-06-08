package com.codecollab.autoreview.model;

import com.codecollab.pullrequest.model.PullRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "auto_review_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutoReviewResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pr_id", nullable = false)
    private PullRequest pullRequest;

    @Column(name = "rule_name", nullable = false)
    private String ruleName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum Severity {
        INFO, WARNING, ERROR
    }
}
