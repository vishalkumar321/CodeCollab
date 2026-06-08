package com.codecollab.comment.model;

import com.codecollab.pullrequest.model.PRFile;
import com.codecollab.pullrequest.model.PullRequest;
import com.codecollab.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pr_id", nullable = false)
    private PullRequest pullRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private PRFile file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "line_number")
    private Integer lineNumber;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "parent_comment_id")
    private Long parentCommentId;

    @Column(name = "is_resolved")
    @Builder.Default
    private Boolean isResolved = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
