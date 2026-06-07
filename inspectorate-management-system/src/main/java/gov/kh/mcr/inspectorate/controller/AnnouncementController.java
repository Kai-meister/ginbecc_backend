package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.request
        .AnnouncementRequest;
import gov.kh.mcr.inspectorate.dto.response.*;
import gov.kh.mcr.inspectorate.security.SecurityUtils;
import gov.kh.mcr.inspectorate.service
        .AnnouncementService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost
        .PreAuthorize;
import org.springframework.validation.annotation
        .Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart
        .MultipartFile;

@Validated
@RestController
@RequestMapping("/api/v1/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService service;
    private final SecurityUtils securityUtils;

    // GET /announcements
    @GetMapping
    @PreAuthorize(
            "hasAuthority('ANNOUNCEMENT_VIEW')")
    public ResponseEntity<ApiResponse<
    PageResponse<AnnouncementResponse>>>
    getAll(
            @RequestParam(
                    defaultValue = "0") int page,
            @RequestParam(
                    defaultValue = "20") int size,
            @RequestParam(required = false)
            String status,
            @RequestParam(required = false)
            String priority) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        service.getAll(
                                page, size,
                                status, priority),
                        "ទទួលបន្ជីប្រកាស"));
    }

    // GET /announcements/{id}
    @GetMapping("/{id}")
    @PreAuthorize(
            "hasAuthority('ANNOUNCEMENT_VIEW')")
    public ResponseEntity<ApiResponse<
    AnnouncementResponse>>
    getById(
            @PathVariable
            @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        service.getById(id),
                        "ទទួលបានប្រកាស"));
    }

    // GET /announcements/{id}/read-status
    @GetMapping("/{id}/read-status")
    @PreAuthorize(
            "hasAnyAuthority('ANNOUNCEMENT_VIEW',"
                    + "'ANNOUNCEMENT_CREATE')")
    public ResponseEntity<ApiResponse<
    AnnouncementReadStatusResponse>>
    getReadStatus(
            @PathVariable
            @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        service.getReadStatus(id),
                        "ស្ថានភាពការអាន"));
    }

    // POST /announcements
    @PostMapping
    @PreAuthorize(
            "hasAuthority('ANNOUNCEMENT_CREATE')")
    public ResponseEntity<ApiResponse<
    AnnouncementResponse>>
    create(
            @Valid @RequestBody
            AnnouncementRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        service.create(request),
                        "បង្កើតជោគជ័យ"));
    }

    // POST /announcements/{id}/attachment
    @PostMapping(
            value = "/{id}/attachment",
            consumes =
                    MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize(
            "hasAuthority('ATTACHMENT_UPLOAD')")
    public ResponseEntity<ApiResponse<
    AnnouncementResponse>>
    uploadAttachment(
            @PathVariable
            @Positive Integer id,
            @RequestParam MultipartFile file) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        service.uploadAttachment(
                                id, file),
                        "Upload ជោគជ័យ"));
    }

    // PUT /announcements/{id}
    @PutMapping("/{id}")
    @PreAuthorize(
            "hasAuthority('ANNOUNCEMENT_UPDATE')")
    public ResponseEntity<ApiResponse<
    AnnouncementResponse>>
    update(
            @PathVariable
            @Positive Integer id,
            @Valid @RequestBody
            AnnouncementRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        service.update(id, request),
                        "កែប្រែជោគជ័យ"));
    }

    // POST /announcements/{id}/mark-read
    @PostMapping("/{id}/mark-read")
    @PreAuthorize(
            "hasAuthority('ANNOUNCEMENT_VIEW')")
    public ResponseEntity<ApiResponse<Void>>
    markAsRead(
            @PathVariable
            @Positive Integer id) {

        // Fix — Get currentOfficerId
        // from SecurityContext
        Integer officerId =
                resolveCurrentOfficerId();

        service.markAsRead(id, officerId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        null, "Mark Read ជោគជ័យ"));
    }

    // DELETE /announcements/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize(
            "hasAuthority('ANNOUNCEMENT_DELETE')")
    public ResponseEntity<ApiResponse<Void>>
    delete(
            @PathVariable
            @Positive Integer id) {

        service.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success(
                        null, "លុបជោគជ័យ"));
    }

    // ── Fix: Get current Officer ID ───────────────
    // Block Admin/SuperAdmin (no officer)
    private Integer resolveCurrentOfficerId() {
        return securityUtils.getCurrentUser()
                .map(user -> {
                    // Fix — must have officer
                    if (user.getOfficer() == null) {
                        throw new
                                gov.kh.mcr.inspectorate
                                        .exception
                                        .BusinessException(
                                "Admin មិនអាច"
                                        + " mark read"
                                        + " — Officer Account"
                                        + " ប៉ុណ្ណោះ");
                    }
                    return user.getOfficer()
                            .getOfficerId();
                })
                .orElseThrow(() ->
                        new gov.kh.mcr.inspectorate
                                .exception.UnauthorizedException(
                                "ត្រូវ Login"));
    }
}