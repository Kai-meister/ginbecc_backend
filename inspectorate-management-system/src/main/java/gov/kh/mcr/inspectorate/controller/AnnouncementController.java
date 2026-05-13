package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.request.AnnouncementRequest;
import gov.kh.mcr.inspectorate.dto.response.*;
import gov.kh.mcr.inspectorate.enums.AttachmentRefType;
import gov.kh.mcr.inspectorate.service.AnnouncementService;
import gov.kh.mcr.inspectorate.service.AttachmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final AttachmentService attachmentService;
    @GetMapping
    @PreAuthorize("hasAuthority('ANNOUNCEMENT_VIEW')")
    public ResponseEntity<ApiResponse<PageResponse<
        AnnouncementResponse>>> getAll(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority) {
        return ResponseEntity.ok(ApiResponse.success(
                announcementService.getAll(page, size, status, priority),
                "ទទួលបន្ជីសេចក្តីប្រកាស"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ANNOUNCEMENT_VIEW')")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> getById(
            @PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(
                announcementService.getById(id),
                "ទទួលបានសេចក្តីប្រកាស"));
    }

    @GetMapping("/{id}/read-status")
    @PreAuthorize("hasAuthority('ANNOUNCEMENT_VIEW')")
    public ResponseEntity<ApiResponse<AnnouncementReadStatusResponse>>
    getReadStatus(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(
                announcementService.getReadStatus(id),
                "ស្ថានភាពអាន"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ANNOUNCEMENT_CREATE')")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> create(
            @Valid @RequestBody AnnouncementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        announcementService.create(request),
                        "បង្កើតជោគជ័យ"));
    }

    @PostMapping("/{id}/recipients/{officerId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Integer id,
            @PathVariable Integer officerId) {
        announcementService.markAsRead(id, officerId);
        return ResponseEntity.ok(
                ApiResponse.success(null, "អានហើយ"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ANNOUNCEMENT_UPDATE')")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody AnnouncementRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                announcementService.update(id, request),
                "កែប្រែជោគជ័យ"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ANNOUNCEMENT_DELETE')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Integer id) {
        announcementService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "លុបជោគជ័យ"));
    }

    @PostMapping(
            value    = "/{id}/attachment",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ANNOUNCEMENT_CREATE')")
    public ResponseEntity<ApiResponse<AttachmentResponse>>
    uploadAnnouncementFile(
            @PathVariable
            @Positive Integer id,
            @RequestParam MultipartFile file) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        attachmentService.upload(
                                file,
                                AttachmentRefType
                                        .ANNOUNCEMENT.getCode(),
                                id),
                        "Upload ឯកសារប្រកាសជោគជ័យ"));
    }
}
