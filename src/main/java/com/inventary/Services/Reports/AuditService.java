package com.inventary.Services.Reports;

import com.inventary.Model.Reports.AuditLog;
import com.inventary.Repository.Reports.AuditRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditService {
    private final AuditRepository auditRepository;

    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    public void logAction(String action, String perfomedBy, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setPerformedBy(perfomedBy);
        auditLog.setPerformedAt(LocalDateTime.now());
        auditLog.setDetails(details);
        auditRepository.save(auditLog);
    }
}
