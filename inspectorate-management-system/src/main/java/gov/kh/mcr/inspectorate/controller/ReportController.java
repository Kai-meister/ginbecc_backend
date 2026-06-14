package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response
        .report.*;
import gov.kh.mcr.inspectorate.exception.BusinessException;
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
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @GetMapping("/officers/excel")
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
                        departmentId, status, from, to),
                "របាយការណ៍មន្ត្រីរាជការ_"
                        + LocalDate.now().format(FMT)
                        + ".xlsx");
    }

    @GetMapping("/officers/preview")
    @PreAuthorize("hasAnyAuthority('REPORT_VIEW'," + "'REPORT_EXPORT')")
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
                        "ទទួលបានទិន្នន័យមន្ត្រីរាជការជោគជ័យ"));
    }

    @GetMapping("/contract-officers/excel")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_VIEW',"
                    + "'REPORT_EXPORT')")
    public ResponseEntity<Resource>
    exportContractOfficers(
            @RequestParam(
                    required = false,
                    defaultValue = "30")
            @Min(value = 1,
                    message = "ចំនួនថ្ងៃអប្បបរមាគឺ ១ ថ្ងៃ")
            @Max(value = 365,
                    message = "ចំនួនថ្ងៃអតិបរមាគឺ ៣៦៥ ថ្ងៃ")
            Integer expiringWithinDays) {

        return excel(
                reportService.exportContractOfficers(
                        expiringWithinDays),
                "របាយការណ៍មន្ត្រីជាប់កិច្ចសន្យា_"
                        + expiringWithinDays + "ថ្ងៃ_"
                        + LocalDate.now().format(FMT)
                        + ".xlsx");
    }

    @GetMapping("/contract-officers/preview")
    @PreAuthorize("hasAnyAuthority('REPORT_VIEW'," + "'REPORT_EXPORT')")
    public ResponseEntity<ApiResponse <List<ContractOfficerReportResponse>>>
    previewContractOfficers(
            @RequestParam(
                    required = false,
                    defaultValue = "30")
            @Min(value = 1,
                    message = "ចំនួនថ្ងៃអប្បបរមាគឺ ១ ថ្ងៃ")
            @Max(value = 365,
                    message = " ចំនួនថ្ងៃអតិបរមាគឺ ៣៦៥ ថ្ងៃ")
            Integer expiringWithinDays) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        reportService
                                .previewContractOfficers(
                                        expiringWithinDays),
                        "ទទួលបានទិន្នន័យមន្ត្រីជាប់កិច្ចសន្យាជោគជ័យ"));
    }

    @GetMapping("/documents/excel")
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

        validateDateRange(from, to);

        return excel(
                reportService.exportDocuments(
                        officerId, status,
                        typeId, from, to),
                "របាយការណ៍ឯកសារ_"
                        + LocalDate.now().format(FMT)
                        + ".xlsx");
    }

    @GetMapping("/documents/preview")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_VIEW',"
                    + "'REPORT_EXPORT',"
                    + "'DOCUMENT_VIEW_ALL')")
    public ResponseEntity<ApiResponse<
    List<DocumentReportResponse>>>
    previewDocuments(
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

        validateDateRange(from, to);

        return ResponseEntity.ok(
                ApiResponse.success(
                        reportService.previewDocuments(
                                officerId, status,
                                typeId, from, to),
                        "ទទួលបានទិន្នន័យឯកសារជោគជ័យ"));
    }

    @GetMapping("/approvals/excel")
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

        validateDateRange(from, to);

        return excel(
                reportService.exportApprovals(
                        status, from, to),
                "របាយការណ៍អនុម័ត_"
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
                                status, from, to),
                        "ទទួលបានទិន្នន័យការអនុម័តជោគជ័យ"));
    }

    @GetMapping("/meetings/excel")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_VIEW',"
                    + "'REPORT_EXPORT')")
    public ResponseEntity<Resource>
    exportMeetings(
            @RequestParam(required = false)
            @Min(value = 1,
                    message = "ខែត្រូវស្ថិតនៅចន្លោះ ១ ដល់ ១២")
            @Max(value = 12,
                    message = "ខែត្រូវស្ថិតនៅចន្លោះ ១ ដល់ ១២")
            Integer month,

            @RequestParam(required = false)
            @Min(value = 2000,
                    message = "ឆ្នាំត្រូវចាប់ពី ២០០០ ឡើងទៅ")
            @Max(value = 2100,
                    message = "ឆ្នាំត្រូវត្រឹមឆ្នាំ ២១០០")
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
                "របាយការណ៍កិច្ចប្រជុំ_" + y + "_"
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
            @Min(value = 1,
                    message = "ខែត្រូវស្ថិតនៅចន្លោះ ១ ដល់ ១២")
            @Max(value = 12,
                    message = "ខែត្រូវស្ថិតនៅចន្លោះ ១ ដល់ ១២")
            Integer month,
            @RequestParam(required = false)
            @Min(value = 2000,
                    message = "ឆ្នាំត្រូវចាប់ពី ២០០០ ឡើងទៅ")
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
                        "ទទួលបានទិន្នន័យកិច្ចប្រជុំជោគជ័យ"));
    }

    @GetMapping("/meeting-minutes/excel")
    @PreAuthorize(
            "hasAnyAuthority('REPORT_VIEW',"
                    + "'MEETING_MINUTE_VIEW')")
    public ResponseEntity<Resource>
    exportMeetingMinutes(
            @RequestParam(required = false)
            @Min(value = 1,
                    message = "ខែត្រូវស្ថិតនៅចន្លោះ ១ ដល់ ១២")
            @Max(value = 12,
                    message = "ខែត្រូវស្ថិតនៅចន្លោះ ១ ដល់ ១២")
            Integer month,
            @RequestParam(required = false)
            @Min(value = 2000,
                    message = "ឆ្នាំត្រូវចាប់ពី ២០០០ ឡើងទៅ")
            Integer year,
            @RequestParam(required = false)
            Long meetingId) {

        int m = month != null ? month
                : LocalDate.now().getMonthValue();
        int y = year != null ? year
                : LocalDate.now().getYear();

        return excel(
                reportService
                        .exportMeetingMinutes(m, y, meetingId),
                "របាយការណ៍កំណត់ហេតុប្រជុំ_" + y + "_"
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
            @Min(value = 1,
                    message = "ខែត្រូវស្ថិតនៅចន្លោះ ១ ដល់ ១២")
            @Max(value = 12,
                    message = "ខែត្រូវស្ថិតនៅចន្លោះ ១ ដល់ ១២")
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
                                .previewMeetingMinutes(m, y, meetingId),
                        "ទទួលបានទិន្នន័យកំណត់ហេតុប្រជុំជោគជ័យ"));
    }

    @GetMapping("/announcements/excel")
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
                "របាយការណ៍សេចក្តីជូនដំណឹង_"
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
                        reportService.previewAnnouncements(
                                status, priority, from, to),
                        "ទទួលបានទិន្នន័យសេចក្តីជូនដំណឹងជោគជ័យ"));
    }

    @GetMapping("/audit-logs/excel")
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
                "របាយការណ៍កំណត់ហេតុសកម្មភាព_"
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
                        "ទទួលបានទិន្នន័យកំណត់ហេតុសកម្មភាពជោគជ័យ"));
    }

    @GetMapping("/notifications/excel")
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
        validateNotificationType(type);

        return excel(
                reportService.exportNotifications(
                        userId, type, isRead, from, to),
                "របាយការណ៍ការជូនដំណឹង_"
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
        validateNotificationType(type);

        return ResponseEntity.ok(
                ApiResponse.success(
                        reportService.previewNotifications(
                                userId, type,
                                isRead, from, to),
                        "ទទួលបានទិន្នន័យការជូនដំណឹងជោគជ័យ"));
    }

    @GetMapping("/users/excel")
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
                "របាយការណ៍អ្នកប្រើប្រាស់_"
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
                        "ទទួលបានទិន្នន័យអ្នកប្រើប្រាស់ជោគជ័យ"));
    }

    private void validateDateRange(
            LocalDate from, LocalDate to) {

        if (from != null && to != null
                && from.isAfter(to)) {
            throw new
                    gov.kh.mcr.inspectorate
                            .exception.BusinessException(
                    "កាលបរិច្ឆេទចាប់ផ្តើមមិនអាចក្រោយកាលបរិច្ឆេទបញ្ចប់បានឡើយ");
        }

        if (from != null
                && from.isAfter(
                LocalDate.now())) {
            throw new
                    gov.kh.mcr.inspectorate
                            .exception.BusinessException(
                    "កាលបរិច្ឆេទចាប់ផ្តើមមិនអាចជាថ្ងៃអនាគតបានឡើយ");
        }
    }
//
//    private void validatePriority(
//            String priority) {
//        if (priority == null
//                || priority.isBlank()) return;
//
//        java.util.Set<String> valid =
//                java.util.Set.of(
//                        "LOW", "MEDIUM",
//                        "HIGH", "URGENT");
//
//        if (!valid.contains(
//                priority.toUpperCase())) {
//            throw new
//                    gov.kh.mcr.inspectorate
//                            .exception.BusinessException(
//                    "priority មិនត្រឹមត្រូវ: ["
//                            + priority + "] — ប្រើ: "
//                            + "LOW, MEDIUM, HIGH, URGENT");
//        }
//    }

    private void validateNotificationType(
            String type) {
        if (type == null
                || type.isBlank()) return;

        try {
            gov.kh.mcr.inspectorate.enums
                    .NotificationType
                    .valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                    "ប្រភេទនៃការជូនដំណឹងមិនត្រឹមត្រូវ៖ [" + type + "]។ " +
                            "សូមជ្រើសរើសប្រភេទមួយក្នុងចំណោម៖ កិច្ចប្រជុំ, ឯកសារ, សេចក្តីជូនដំណឹង, ឬ ប្រព័ន្ធ។");
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
                .body(new ByteArrayResource(data));
    }
}