package com.codecollab.pullrequest.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pr_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PRFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pr_id", nullable = false)
    private PullRequest pullRequest;

    @Column(nullable = false)
    private String filename;

    @Column(name = "base_content", columnDefinition = "TEXT")
    private String baseContent;

    @Column(name = "changed_content", columnDefinition = "TEXT")
    private String changedContent;
}
