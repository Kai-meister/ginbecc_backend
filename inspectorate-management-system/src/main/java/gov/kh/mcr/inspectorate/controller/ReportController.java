package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.response
        .ApiResponse;
import gov.kh.mcr.inspectorate.dto.response
        .report.*;
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
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd");

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

        validateDateRange(from, to);

        return excel(
                reportService.exportOfficers(
                        departmentId, status,
                        from, to),
                "officers_"
                        + LocalDate.now().format(FMT)
                        + ".xlsx");
    }

    @GetMapping("/officers/preview")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_VIEW',"
                    + "'REPORT_EXPORT')")
    public ResponseEntity<ApiResponse<
    List<OfficerReportResponse>>>
    previewOfficers(
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

        validateDateRange(from, to);

        return ResponseEntity.ok(
                ApiResponse.success(
                        reportService.previewOfficers(
                                departmentId, status,
                                from, to),
                        "Officers Preview"));
    }

    @GetMapping("/contract-officers")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_VIEW',"
                    + "'REPORT_EXPORT')")
    public ResponseEntity<Resource>
    exportContractOfficers(
            @RequestParam(
                    required = false,
                    defaultValue = "30")
            @Min(1) @Max(365)
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

    @GetMapping("/contract-officers/preview")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_VIEW',"
                    + "'REPORT_EXPORT')")
    public ResponseEntity<ApiResponse<
    List<ContractOfficerReportResponse>>>
    previewContractOfficers(
            @RequestParam(
                    required = false,
                    defaultValue = "30")
            @Min(1) @Max(365)
            Integer expiringWithinDays) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        reportService
                                .previewContractOfficers(
                                        expiringWithinDays),
                        "Contract Officers Preview"));
    }


    @GetMapping("/documents")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_VIEW',"
                    + "'REPORT_EXPORT',"
                    + "'DOCUMENT_VIEW_ALL')")
    public ResponseEntity<Resource>
    exportDocuments(
            @RequestParam(required = false)
            Integer userId,
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

        validateDateRange(from, to);

        return excel(
                reportService.exportDocuments(
                        userId, status,
                        typeId, from, to),
                "documents_"
                        + LocalDate.now().format(FMT)
                        + ".xlsx");
    }

    @GetMapping("/documents/preview")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_VIEW',"
                    + "'DOCUMENT_VIEW_ALL')")
    public ResponseEntity<ApiResponse<
    List<DocumentReportResponse>>>
    previewDocuments(
            @RequestParam(required = false)
            Integer userId,
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

        validateDateRange(from, to);

        return ResponseEntity.ok(
                ApiResponse.success(
                        reportService.previewDocuments(
                                userId, status,
                                typeId, from, to),
                        "Documents Preview"));
    }


    @GetMapping("/approvals")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_VIEW',"
                    + "'APPROVAL_VIEW')")
    public ResponseEntity<Resource>
    exportApprovals(
            @RequestParam(required = false)
            String status,
            @RequestParam(required = false)
            Integer userId,
            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE)
            LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE)
            LocalDate to) {

        validateDateRange(from, to);

        return excel(
                reportService.exportApprovals(
                        status, userId,
                        from, to),
                "approvals_"
                        + LocalDate.now().format(FMT)
                        + ".xlsx");
    }

    @GetMapping("/approvals/preview")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_VIEW',"
                    + "'APPROVAL_VIEW')")
    public ResponseEntity<ApiResponse<
    List<ApprovalReportResponse>>>
    previewApprovals(
            @RequestParam(required = false)
            String status,
            @RequestParam(required = false)
            Integer userId,
            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE)
            LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE)
            LocalDate to) {

        validateDateRange(from, to);

        return ResponseEntity.ok(
                ApiResponse.success(
                        reportService.previewApprovals(
                                status, userId,
                                from, to),
                        "Approvals Preview"));
    }

    @GetMapping("/meetings")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_VIEW',"
                    + "'MEETING_VIEW')")
    public ResponseEntity<Resource>
    exportMeetings(
            @RequestParam(required = false)
            @Min(1) @Max(12)
            Integer month,
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

    @GetMapping("/meetings/preview")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_VIEW',"
                    + "'MEETING_VIEW')")
    public ResponseEntity<ApiResponse<
    List<MeetingReportResponse>>>
    previewMeetings(
            @RequestParam(required = false)
            @Min(1) @Max(12)
            Integer month,
            @RequestParam(required = false)
            Integer year,
            @RequestParam(required = false)
            String status) {

        int m = month != null ? month
                : LocalDate.now().getMonthValue();
        int y = year != null ? year
                : LocalDate.now().getYear();

        return ResponseEntity.ok(
                ApiResponse.success(
                        reportService.previewMeetings(
                                m, y, status),
                        "Meetings Preview"));
    }

    @GetMapping("/meeting-minutes")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_VIEW',"
                    + "'MEETING_MINUTE_VIEW')")
    public ResponseEntity<Resource>
    exportMeetingMinutes(
            @RequestParam(required = false)
            @Min(1) @Max(12)
            Integer month,
            @RequestParam(required = false)
            Integer year,
            @RequestParam(required = false)
            Long meetingId) {

        int m = month != null ? month
                : LocalDate.now().getMonthValue();
        int y = year != null ? year
                : LocalDate.now().getYear();

        return excel(
                reportService
                        .exportMeetingMinutes(
                                m, y, meetingId),
                "meeting_minutes_" + y + "_"
                        + String.format("%02d", m)
                        + ".xlsx");
    }

    @GetMapping("/meeting-minutes/preview")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_VIEW',"
                    + "'MEETING_MINUTE_VIEW')")
    public ResponseEntity<ApiResponse<
    List<MeetingMinuteReportResponse>>>
    previewMeetingMinutes(
            @RequestParam(required = false)
            @Min(1) @Max(12)
            Integer month,
            @RequestParam(required = false)
            Integer year,
            @RequestParam(required = false)
            Long meetingId) {

        int m = month != null ? month
                : LocalDate.now().getMonthValue();
        int y = year != null ? year
                : LocalDate.now().getYear();

        return ResponseEntity.ok(
                ApiResponse.success(
                        reportService
                                .previewMeetingMinutes(
                                        m, y, meetingId),  // ✅ pass meetingId
                        "Minutes Preview"));
    }


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

        validateDateRange(from, to);

        return excel(
                reportService.exportAnnouncements(
                        status, priority, from, to),
                "announcements_"
                        + LocalDate.now().format(FMT)
                        + ".xlsx");
    }

    @GetMapping("/announcements/preview")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_VIEW',"
                    + "'ANNOUNCEMENT_VIEW')")
    public ResponseEntity<ApiResponse<
    List<AnnouncementReportResponse>>>
    previewAnnouncements(
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

        validateDateRange(from, to);

        return ResponseEntity.ok(
                ApiResponse.success(
                        reportService
                                .previewAnnouncements(
                                        status, priority,
                                        from, to),
                        "Announcements Preview"));
    }

    @GetMapping(
            "/announcements/{id}/recipients")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_VIEW',"
                    + "'ANNOUNCEMENT_VIEW')")
    public ResponseEntity<Resource>
    exportRecipients(
            @PathVariable
            @Positive Integer id,
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

        validateDateRange(from, to);

        return excel(
                reportService
                        .exportAnnouncementRecipients(
                                id, isRead, from, to),
                "ann_recipients_" + id + "_"
                        + LocalDate.now().format(FMT)
                        + ".xlsx");
    }

    @GetMapping(
            "/announcements/{id}"
                    + "/recipients/preview")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_VIEW',"
                    + "'ANNOUNCEMENT_VIEW')")
    public ResponseEntity<ApiResponse<
            List<
    AnnouncementRecipientReportResponse>>>
    previewRecipients(
            @PathVariable
            @Positive Integer id,
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

        validateDateRange(from, to);

        return ResponseEntity.ok(
                ApiResponse.success(
                        reportService
                                .previewAnnouncementRecipients(
                                        id, isRead,
                                        from, to),
                        "Recipients Preview"));
    }


    @GetMapping("/audit-logs")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_ADVANCED',"
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

        validateDateRange(from, to);

        return excel(
                reportService.exportAuditLogs(
                        userId, action,
                        entityType, from, to),
                "audit_logs_"
                        + LocalDate.now().format(FMT)
                        + ".xlsx");
    }

    @GetMapping("/audit-logs/preview")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_ADVANCED',"
                    + "'LOG_VIEW')")
    public ResponseEntity<ApiResponse<
    List<AuditLogReportResponse>>>
    previewAuditLogs(
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

        validateDateRange(from, to);

        return ResponseEntity.ok(
                ApiResponse.success(
                        reportService.previewAuditLogs(
                                userId, action,
                                entityType, from, to),
                        "Audit Logs Preview"));
    }


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

        validateDateRange(from, to);

        return excel(
                reportService.exportNotifications(
                        userId, type, isRead,
                        from, to),
                "notifications_"
                        + LocalDate.now().format(FMT)
                        + ".xlsx");
    }

    @GetMapping("/notifications/preview")
    @PreAuthorize(
            "hasAuthority('REPORT_ADVANCED')")
    public ResponseEntity<ApiResponse<
    List<NotificationReportResponse>>>
    previewNotifications(
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

        validateDateRange(from, to);

        return ResponseEntity.ok(
                ApiResponse.success(
                        reportService
                                .previewNotifications(
                                        userId, type,
                                        isRead, from, to),
                        "Notifications Preview"));
    }


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

    @GetMapping("/users/preview")
    @PreAuthorize(
            "hasAuthority('REPORT_ADVANCED')")
    public ResponseEntity<ApiResponse<
    List<UserReportResponse>>>
    previewUsers(
            @RequestParam(required = false)
            Integer roleId,
            @RequestParam(required = false)
            String status) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        reportService.previewUsers(
                                roleId, status),
                        "Users Preview"));
    }



    private void validateDateRange(
            LocalDate from, LocalDate to) {

        if (from != null && to != null
                && from.isAfter(to)) {
            throw new
                    gov.kh.mcr.inspectorate
                            .exception.BusinessException(
                    "from (" + from + ")"
                            + " ត្រូវ <= to ("
                            + to + ")");
        }

        if (from != null
                && from.isAfter(
                LocalDate.now())) {
            throw new
                    gov.kh.mcr.inspectorate
                            .exception.BusinessException(
                    "from មិនអាចជា"
                            + "ថ្ងៃអនាគត");
        }
    }

    private ResponseEntity<Resource> excel(
            byte[] data, String filename) {
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
                .body(new ByteArrayResource(
                        data));
    }


}