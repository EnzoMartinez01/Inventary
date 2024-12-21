package com.inventary.Controller.Reports;

import com.inventary.Services.Reports.AuditService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit")
public class AuditController {
    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @PostMapping("/logAction")
    public ResponseEntity<Void> logAction(
            @RequestParam String action,
            @RequestParam String performedBy,
            @RequestParam String details
    ) {
        auditService.logAction(action, performedBy, details);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
