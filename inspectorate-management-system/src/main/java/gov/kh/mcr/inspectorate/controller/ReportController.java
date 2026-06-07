package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.service
        .ReportService;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.*;
import org.springframework.format.annotation
        .DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost
        .PreAuthorize;
import org.springframework.validation.annotation
        .Validated;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Validated
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // 1. OFFICERS
    @GetMapping("/officers")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_VIEW',"
                    + "'REPORT_EXPORT')")
    public ResponseEntity<Resource>
    exportOfficers(
            @RequestParam(required = false)
            Integer departmentId,
            @RequestParam(required = false)
            String status,
            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE)
            LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE)
            LocalDate to) {

        return excel(
                reportService.exportOfficers(
                        departmentId, status,
                        from, to),
                "officers_"
                        + LocalDate.now().format(FMT)
                        + ".xlsx");
    }


    // 2. CONTRACT OFFICERS
    @GetMapping("/contract-officers")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_VIEW',"
                    + "'REPORT_EXPORT')")
    public ResponseEntity<Resource>
    exportContractOfficers(
            @RequestParam(
                    required = false,
                    defaultValue = "30")
            Integer expiringWithinDays) {

        return excel(
                reportService
                        .exportContractOfficers(
                                expiringWithinDays),
                "contract_officers_"
                        + expiringWithinDays + "d_"
                        + LocalDate.now().format(FMT)
                        + ".xlsx");
    }

    // 3. DOCUMENTS
    @GetMapping("/documents")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_VIEW',"
                    + "'REPORT_EXPORT',"
                    + "'DOCUMENT_VIEW_ALL')")
    public ResponseEntity<Resource>
    exportDocuments(
            @RequestParam(required = false)
            Integer officerId,
            @RequestParam(required = false)
            String status,
            @RequestParam(required = false)
            Integer typeId,
            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE)
            LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE)
            LocalDate to) {

        return excel(
                reportService.exportDocuments(
                        officerId, status,
                        typeId, from, to),
                "documents_"
                        + LocalDate.now().format(FMT)
                        + ".xlsx");
    }

    // 4. APPROVALS
    @GetMapping("/approvals")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_VIEW',"
                    + "'REPORT_EXPORT',"
                    + "'APPROVAL_VIEW')")
    public ResponseEntity<Resource>
    exportApprovals(
            @RequestParam(required = false)
            String status,
            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE)
            LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE)
            LocalDate to) {

        return excel(
                reportService.exportApprovals(
                        status, from, to),
                "approvals_"
                        + LocalDate.now().format(FMT)
                        + ".xlsx");
    }

    // 5. MEETINGS
    @GetMapping("/meetings")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_VIEW',"
                    + "'REPORT_EXPORT')")
    public ResponseEntity<Resource>
    exportMeetings(
            @RequestParam(required = false)
            @Min(1) @Max(12) Integer month,
            @RequestParam(required = false)
            Integer year,
            @RequestParam(required = false)
            String status) {

        int m = month != null ? month
                : LocalDate.now().getMonthValue();
        int y = year != null ? year
                : LocalDate.now().getYear();

        return excel(
                reportService.exportMeetings(
                        m, y, status),
                "meetings_" + y + "_"
                        + String.format("%02d", m)
                        + ".xlsx");
    }

    // 6. MEETING MINUTES
    @GetMapping("/meeting-minutes")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_VIEW',"
                    + "'REPORT_EXPORT',"
                    + "'MEETING_MINUTE_VIEW')")
    public ResponseEntity<Resource>
    exportMeetingMinutes(
            @RequestParam(required = false)
            @Min(1) @Max(12) Integer month,
            @RequestParam(required = false)
            Integer year) {

        int m = month != null ? month
                : LocalDate.now().getMonthValue();
        int y = year != null ? year
                : LocalDate.now().getYear();

        return excel(
                reportService
                        .exportMeetingMinutes(m, y),
                "meeting_minutes_" + y + "_"
                        + String.format("%02d", m)
                        + ".xlsx");
    }

    // 7. ANNOUNCEMENTS
    @GetMapping("/announcements")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_VIEW',"
                    + "'REPORT_EXPORT')")
    public ResponseEntity<Resource>
    exportAnnouncements(
            @RequestParam(required = false)
            String status,
            @RequestParam(required = false)
            String priority,
            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE)
            LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE)
            LocalDate to) {

        return excel(
                reportService.exportAnnouncements(
                        status, priority, from, to),
                "announcements_"
                        + LocalDate.now().format(FMT)
                        + ".xlsx");
    }

    // 8. AUDIT LOGS
    @GetMapping("/audit-logs")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_ADVANCED',"
                    + "'REPORT_EXPORT',"
                    + "'LOG_VIEW')")
    public ResponseEntity<Resource>
    exportAuditLogs(
            @RequestParam(required = false)
            Integer userId,
            @RequestParam(required = false)
            String action,
            @RequestParam(required = false)
            String entityType,
            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE)
            LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE)
            LocalDate to) {

        return excel(
                reportService.exportAuditLogs(
                        userId, action,
                        entityType, from, to),
                "audit_logs_"
                        + LocalDate.now().format(FMT)
                        + ".xlsx");
    }

    // 9. NOTIFICATIONS
    @GetMapping("/notifications")
    @PreAuthorize(
            "hasAuthority('REPORT_ADVANCED')")
    public ResponseEntity<Resource>
    exportNotifications(
            @RequestParam(required = false)
            Integer userId,
            @RequestParam(required = false)
            String type,
            @RequestParam(required = false)
            Boolean isRead,
            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE)
            LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE)
            LocalDate to) {

        return excel(
                reportService.exportNotifications(
                        userId, type, isRead,
                        from, to),
                "notifications_"
                        + LocalDate.now().format(FMT)
                        + ".xlsx");
    }

    // 10. USERS
    @GetMapping("/users")
    @PreAuthorize(
            "hasAuthority('REPORT_ADVANCED')")
    public ResponseEntity<Resource>
    exportUsers(
            @RequestParam(required = false)
            Integer roleId,
            @RequestParam(required = false)
            String status) {

        return excel(
                reportService.exportUsers(
                        roleId, status),
                "users_"
                        + LocalDate.now().format(FMT)
                        + ".xlsx");
    }

    private ResponseEntity<Resource> excel(
            byte[] data,
            String filename) {

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + filename + "\"")
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd"
                                        + ".openxmlformats-"
                                        + "officedocument"
                                        + ".spreadsheetml.sheet"))
                .contentLength(data.length)
                .body(
                        new ByteArrayResource(data));
    }
}