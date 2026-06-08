package com.codecollab.audit;

import com.codecollab.audit.model.AuditLog;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping("/{prId}/audit")
    public ResponseEntity<List<AuditLog>> getAuditTrail(@PathVariable Long prId) {
        return ResponseEntity.ok(auditLogService.getAuditTrail(prId));
    }
}
