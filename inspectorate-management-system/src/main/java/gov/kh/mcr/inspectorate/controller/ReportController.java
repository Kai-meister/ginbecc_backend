package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    private static final String XLSX_MEDIA =
            "application/vnd.openxmlformats-officedocument"
                    + ".spreadsheetml.sheet";

    @GetMapping("/officers")
    @PreAuthorize("hasAnyAuthority('REPORT_EXPORT','DAILY_REPORT')")
    public ResponseEntity<byte[]> exportOfficers(
            @RequestParam(required = false) String format,
            @RequestParam(required = false) Integer dept)
            throws Exception {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=officers_report.xlsx")
                .contentType(MediaType.parseMediaType(XLSX_MEDIA))
                .body(reportService.exportOfficersToExcel(dept));
    }

    @GetMapping("/meetings")
    @PreAuthorize("hasAnyAuthority('REPORT_EXPORT','DAILY_REPORT')")
    public ResponseEntity<byte[]> exportMeetings(
            @RequestParam(required = false) String format,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to)
            throws Exception {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=meetings_report.xlsx")
                .contentType(MediaType.parseMediaType(XLSX_MEDIA))
                .body(reportService.exportMeetingsToExcel(from, to));
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasAuthority('AUDIT_VIEW')")
    public ResponseEntity<byte[]> exportAuditLogs(
            @RequestParam(required = false) String format)
            throws Exception {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=audit_logs_report.xlsx")
                .contentType(MediaType.parseMediaType(XLSX_MEDIA))
                .body(reportService.exportAuditLogsToExcel());
    }
}
