package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.request
        .AnnouncementRequest;
import gov.kh.mcr.inspectorate.dto.response.*;
import gov.kh.mcr.inspectorate.exception.BusinessException;
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
            String priority,
            @RequestParam(
                    required = false,
                    defaultValue = "false")
            Boolean includeExpired) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        service.getAll(
                                page, size,
                                status, priority,
                                includeExpired),
                        "ទទួលបញ្ជីប្រកាសដោយជោគជ័យ"));
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
                        "ទទួលបានព័ត៌មានប្រកាសដោយជោគជ័យ"));
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
                        "ទទួលបានស្ថានភាពការអានដោយជោគជ័យ"));
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
                        "បានបង្កើតការប្រកាសដោយជោគជ័យ"));
    }

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

        if (file == null || file.isEmpty()) {
            throw new BusinessException(
                    "សូមជ្រើសរើសឯកសារសម្រាប់បញ្ចូល");
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        service.uploadAttachment(id, file),
                        "ឯកសារត្រូវបានបញ្ចូលដោយជោគជ័យ"));
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
                        "បានកែប្រែព័ត៌មានប្រកាសដោយជោគជ័យ"));
    }

    @PostMapping("/{id}/mark-read")
    @PreAuthorize(
            "hasAuthority('ANNOUNCEMENT_VIEW')")
    public ResponseEntity<ApiResponse<Void>>
    markAsRead(
            @PathVariable
            @Positive Integer id) {

        service.markAsRead(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        null, "ស្ថានភាពប្រកាសត្រូវបានកំណត់ថា 'បានអាន' ដោយជោគជ័យ"));
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
                        null, "បានលុបដោយជោគជ័យ"));
    }

@GetMapping("/{id}/attachment/download")
@PreAuthorize(
        "hasAuthority('ANNOUNCEMENT_VIEW')")
public ResponseEntity<Void> download(
        @PathVariable
        @Positive Integer id) {

    String url =
            service.getAttachmentUrl(id);

    return ResponseEntity
            .status(HttpStatus.FOUND)
            .header(
                    HttpHeaders.LOCATION, url)
            .build();
}

}