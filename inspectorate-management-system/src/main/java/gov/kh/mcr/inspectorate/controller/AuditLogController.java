package gov.kh.mcr.inspectorate.controller;
import gov.kh.mcr.inspectorate.dto.response.ActivityLogResponse;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@Validated
@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final ActivityLogService activityLogService;

    // GET /audit-logs
    @GetMapping
    @PreAuthorize("hasAuthority('LOG_VIEW')")
    public ResponseEntity<ApiResponse<
            PageResponse<ActivityLogResponse>>>
    getLogs(
            @RequestParam(required = false)
            Integer userId,
            @RequestParam(required = false)
            String action,
            @RequestParam(required = false)
            String entityType,
            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat
                            .ISO.DATE_TIME)
            LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat
                            .ISO.DATE_TIME)
            LocalDateTime to,
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "20")
            int size) {

        return ResponseEntity.ok(ApiResponse.success(
                        activityLogService.getLogs(
                                userId, action,
                                entityType, from, to,
                                page, size),
                        "បាទទួលបានប្រវត្តិសកម្មភាព"));
    }

    // GET /audit-logs/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LOG_VIEW')")
    public ResponseEntity<ApiResponse<
    ActivityLogResponse>>
    getById(
            @PathVariable
            @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        activityLogService.getById(id),
                        "ទទួលបាន Log"));
    }
}