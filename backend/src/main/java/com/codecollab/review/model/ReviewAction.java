package com.codecollab.review.model;

import com.codecollab.pullrequest.model.PullRequest;
import com.codecollab.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_actions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pr_id", nullable = false)
    private PullRequest pullRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewActionType action;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum ReviewActionType {
        APPROVE, REQUEST_CHANGES, COMMENT
    }
}
