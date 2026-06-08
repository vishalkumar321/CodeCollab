package com.codecollab.pullrequest;

import com.codecollab.autoreview.AutoReviewService;
import com.codecollab.autoreview.model.AutoReviewResult;
import com.codecollab.diff.DiffLine;
import com.codecollab.diff.DiffService;
import com.codecollab.events.PREventPublisher;
import com.codecollab.pullrequest.model.PRFile;
import com.codecollab.pullrequest.model.PRReviewer;
import com.codecollab.pullrequest.model.PullRequest;
import com.codecollab.pullrequest.model.PullRequest.PRStatus;
import com.codecollab.repository.RepoRepository;
import com.codecollab.repository.model.Repository;
import com.codecollab.sla.SLAService;
import com.codecollab.sla.model.SLATracker;
import com.codecollab.user.UserRepository;
import com.codecollab.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PRService {

    private final PRRepository prRepository;
    private final PRFileRepository prFileRepository;
    private final PRReviewerRepository prReviewerRepository;
    private final UserRepository userRepository;
    private final RepoRepository repoRepository;
    private final PRStateMachine stateMachine;
    private final PREventPublisher eventPublisher;
    private final SLAService slaService;
    private final DiffService diffService;
    private final AutoReviewService autoReviewService;

    @Transactional
    public PullRequest createPR(Long authorId, CreatePRRequest request) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Author not found"));
        Repository repo = repoRepository.findById(request.repoId())
                .orElseThrow(() -> new IllegalArgumentException("Repository not found"));

        // Use PRBuilder pattern
        PRBuilder builder = new PRBuilder()
                .title(request.title())
                .description(request.description())
                .repository(repo)
                .author(author)
                .status(PRStatus.DRAFT);

        PullRequest pr = prRepository.save(builder.build());

        // Save files
        if (request.files() != null) {
            for (CreatePRRequest.FileData fd : request.files()) {
                PRFile file = PRFile.builder()
                        .pullRequest(pr)
                        .filename(fd.filename())
                        .baseContent(fd.baseContent())
                        .changedContent(fd.changedContent())
                        .build();
                prFileRepository.save(file);
            }
        }

        // Assign reviewers (cannot assign self)
        if (request.reviewerIds() != null) {
            for (Long reviewerId : request.reviewerIds()) {
                if (reviewerId.equals(authorId)) continue; // Skip self
                User reviewer = userRepository.findById(reviewerId).orElse(null);
                if (reviewer != null) {
                    PRReviewer prReviewer = PRReviewer.builder()
                            .pullRequest(pr)
                            .reviewer(reviewer)
                            .build();
                    prReviewerRepository.save(prReviewer);
                }
            }
        }

        return prRepository.findById(pr.getId()).orElse(pr);
    }

    @Transactional
    public PullRequest updateStatus(Long prId, Long actorId, PRStatus newStatus) {
        PullRequest pr = prRepository.findById(prId)
                .orElseThrow(() -> new IllegalArgumentException("PR not found: " + prId));
        User actor = userRepository.findById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + actorId));

        PRStatus oldStatus = pr.getStatus();
        stateMachine.validateTransition(oldStatus, newStatus, actor.getRole());

        pr.setStatus(newStatus);
        PullRequest saved = prRepository.save(pr);

        // Publish state change event (triggers audit log + SLA + auto-review listeners)
        eventPublisher.publishStateChange(saved, oldStatus, newStatus, actor);

        // Check for merge conflicts when opening
        if (newStatus == PRStatus.OPEN) {
            checkMergeConflicts(saved);
        }

        return saved;
    }

    private void checkMergeConflicts(PullRequest pr) {
        List<PullRequest> openPRs = prRepository.findOpenPRsByRepo(pr.getRepository().getId());
        Set<String> prFilenames = pr.getFiles().stream()
                .map(PRFile::getFilename).collect(Collectors.toSet());

        for (PullRequest other : openPRs) {
            if (other.getId().equals(pr.getId())) continue;
            boolean hasConflict = other.getFiles().stream()
                    .anyMatch(f -> prFilenames.contains(f.getFilename()));
            if (hasConflict) {
                // Flag both PRs — in a real system, you'd store this; here we log it
                // The conflict is surfaced through a dedicated endpoint
            }
        }
    }

    @Transactional(readOnly = true)
    public List<PullRequest> getAllPRs(Long repoId, String status, Long reviewerId) {
        if (repoId != null) return prRepository.findByRepositoryId(repoId);
        if (status != null) return prRepository.findByStatus(PRStatus.valueOf(status.toUpperCase()));
        if (reviewerId != null) return prRepository.findByReviewerId(reviewerId);
        return prRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PullRequest getPRById(Long id) {
        return prRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("PR not found: " + id));
    }

    @Transactional
    public void deletePR(Long prId, Long actorId) {
        PullRequest pr = prRepository.findById(prId)
                .orElseThrow(() -> new IllegalArgumentException("PR not found: " + prId));
        if (pr.getStatus() != PRStatus.DRAFT) {
            throw new IllegalStateException("PR can only be deleted in DRAFT state");
        }
        if (!pr.getAuthor().getId().equals(actorId)) {
            throw new IllegalStateException("Only the author can delete their PR");
        }
        prRepository.delete(pr);
    }

    @Transactional(readOnly = true)
    public List<DiffLine> getFileDiff(Long fileId) {
        return diffService.getDiff(fileId);
    }

    @Transactional(readOnly = true)
    public List<PRFile> getPRFiles(Long prId) {
        return prFileRepository.findByPullRequestId(prId);
    }

    @Transactional(readOnly = true)
    public List<AutoReviewResult> getAutoReviewResults(Long prId) {
        return autoReviewService.getResults(prId);
    }

    @Transactional(readOnly = true)
    public List<String> getMergeConflicts(Long prId) {
        PullRequest pr = getPRById(prId);
        List<PullRequest> openPRs = prRepository.findOpenPRsByRepo(pr.getRepository().getId());
        Set<String> prFilenames = pr.getFiles().stream()
                .map(PRFile::getFilename).collect(Collectors.toSet());

        List<String> conflicts = new ArrayList<>();
        for (PullRequest other : openPRs) {
            if (other.getId().equals(prId)) continue;
            other.getFiles().stream()
                    .filter(f -> prFilenames.contains(f.getFilename()))
                    .forEach(f -> conflicts.add(
                            String.format("Conflict with PR #%d ('%s') on file: %s",
                                    other.getId(), other.getTitle(), f.getFilename())
                    ));
        }
        return conflicts;
    }

    @Transactional(readOnly = true)
    public Optional<SLATracker> getSLATracker(Long prId) {
        return slaService.getTrackerByPrId(prId);
    }
}
