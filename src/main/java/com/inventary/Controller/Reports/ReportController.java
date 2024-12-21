package com.inventary.Controller.Reports;

import com.inventary.Dto.Movements.InventoryMovementDto;
import com.inventary.Dto.Reports.KardexDto;
import com.inventary.Model.Authentication.Security.CustomUserDetails;
import com.inventary.Model.Movements.InventoryMovement;
import com.inventary.Model.Products.Inventory;
import com.inventary.Services.Reports.ReportService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/inventory")
    public ResponseEntity<Page<Inventory>> generateInventoryReport(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Inventory> inventoryReport = reportService.generateSocketReport(page, size);
        return ResponseEntity.ok(inventoryReport);
    }

    @GetMapping("/movements")
    public ResponseEntity<Page<InventoryMovementDto>> searchMovements(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = getCurrentUserId();
        Page<InventoryMovementDto> movements = reportService.generateMovementReport(warehouseId, startDate, endDate, page, size, userId);
        return ResponseEntity.ok(movements);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return ((CustomUserDetails) userDetails).getUser().getIdUser();
    }

    @GetMapping("/kardex")
    public ResponseEntity<List<KardexDto>> getKardex(
            @RequestParam Long warehouseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<KardexDto> kardex = reportService.generateKardex(warehouseId, startDate, endDate);
            return ResponseEntity.ok(kardex);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        }
    }

    @GetMapping("/kardex/excel")
    public ResponseEntity<InputStreamResource> downloadKardexExcel(
            @RequestParam Long warehouseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate endDate) throws IOException {
        List<KardexDto> kardex = reportService.generateKardex(warehouseId, startDate, endDate);
        ByteArrayInputStream in = reportService.generateKardexExcel(kardex);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=kardex.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(in));
    }
}
