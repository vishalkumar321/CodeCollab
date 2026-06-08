package com.codecollab.admin;

import com.codecollab.audit.model.AuditLog;
import com.codecollab.pullrequest.model.PullRequest;
import com.codecollab.sla.model.SLATracker;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/prs")
    public ResponseEntity<List<PullRequest>> getAllPRs(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long repoId,
            @RequestParam(required = false) Long reviewerId) {
        return ResponseEntity.ok(adminService.getAllPRs(status, repoId, reviewerId));
    }

    @PostMapping("/prs/{prId}/reassign")
    public ResponseEntity<Void> reassignReviewers(
            @PathVariable Long prId,
            @RequestBody Map<String, List<Long>> body,
            HttpServletRequest httpRequest) {
        Long adminId = (Long) httpRequest.getAttribute("userId");
        adminService.reassignReviewers(prId, body.get("reviewerIds"), adminId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/prs/{prId}/status")
    public ResponseEntity<PullRequest> forcePRStatus(
            @PathVariable Long prId,
            @RequestBody Map<String, String> body,
            HttpServletRequest httpRequest) {
        Long adminId = (Long) httpRequest.getAttribute("userId");
        PullRequest.PRStatus newStatus = PullRequest.PRStatus.valueOf(body.get("status").toUpperCase());
        return ResponseEntity.ok(adminService.forcePRStatus(prId, newStatus, adminId));
    }

    @GetMapping("/sla/breached")
    public ResponseEntity<List<SLATracker>> getBreachedSLAs() {
        return ResponseEntity.ok(adminService.getBreachedSLAs());
    }

    @GetMapping("/workload")
    public ResponseEntity<Map<String, Long>> getReviewerWorkload() {
        return ResponseEntity.ok(adminService.getReviewerWorkload());
    }

    @GetMapping("/prs/{prId}/audit")
    public ResponseEntity<List<AuditLog>> getAuditTrail(@PathVariable Long prId) {
        return ResponseEntity.ok(adminService.getAuditTrail(prId));
    }
}
