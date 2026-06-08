package com.codecollab.pullrequest;

import com.codecollab.autoreview.model.AutoReviewResult;
import com.codecollab.diff.DiffLine;
import com.codecollab.pullrequest.model.PRFile;
import com.codecollab.pullrequest.model.PullRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prs")
@RequiredArgsConstructor
public class PRController {

    private final PRService prService;

    @PostMapping
    public ResponseEntity<PullRequest> createPR(
            @RequestBody CreatePRRequest request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        return ResponseEntity.ok(prService.createPR(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<PullRequest>> getAllPRs(
            @RequestParam(required = false) Long repoId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long reviewerId) {
        return ResponseEntity.ok(prService.getAllPRs(repoId, status, reviewerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PullRequest> getPRById(@PathVariable Long id) {
        return ResponseEntity.ok(prService.getPRById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PullRequest> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        PullRequest.PRStatus newStatus = PullRequest.PRStatus.valueOf(body.get("status").toUpperCase());
        return ResponseEntity.ok(prService.updateStatus(id, userId, newStatus));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePR(@PathVariable Long id, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        prService.deletePR(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/files")
    public ResponseEntity<List<PRFile>> getPRFiles(@PathVariable Long id) {
        return ResponseEntity.ok(prService.getPRFiles(id));
    }

    @GetMapping("/files/{fileId}/diff")
    public ResponseEntity<List<DiffLine>> getFileDiff(@PathVariable Long fileId) {
        return ResponseEntity.ok(prService.getFileDiff(fileId));
    }

    @GetMapping("/{id}/auto-review")
    public ResponseEntity<List<AutoReviewResult>> getAutoReview(@PathVariable Long id) {
        return ResponseEntity.ok(prService.getAutoReviewResults(id));
    }

    @GetMapping("/{id}/conflicts")
    public ResponseEntity<List<String>> getMergeConflicts(@PathVariable Long id) {
        return ResponseEntity.ok(prService.getMergeConflicts(id));
    }

    @GetMapping("/{id}/sla")
    public ResponseEntity<?> getSLA(@PathVariable Long id) {
        return prService.getSLATracker(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
