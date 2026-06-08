package com.codecollab.pullrequest.model;

import com.codecollab.repository.model.Repository;
import com.codecollab.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pull_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PullRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PRStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repo_id", nullable = false)
    private Repository repository;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "pullRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PRFile> files = new ArrayList<>();

    @OneToMany(mappedBy = "pullRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PRReviewer> reviewers = new ArrayList<>();

    /**
     * PR State Machine — valid statuses.
     * Transitions enforced in PRStateMachine.java (State Machine pattern).
     */
    public enum PRStatus {
        DRAFT, OPEN, IN_REVIEW, CHANGES_REQUESTED, APPROVED, MERGED, CLOSED
    }
}
