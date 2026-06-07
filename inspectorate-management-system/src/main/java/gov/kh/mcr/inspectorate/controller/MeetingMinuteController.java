package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.request.MeetingMinuteRequest;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response.AttachmentResponse;
import gov.kh.mcr.inspectorate.dto.response.MeetingMinuteResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.enums.AttachmentRefType;
import gov.kh.mcr.inspectorate.service.AttachmentService;
import gov.kh.mcr.inspectorate.service.MeetingMinuteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/meeting-minutes")
@RequiredArgsConstructor
public class MeetingMinuteController {

    private final MeetingMinuteService meetingMinuteService;
    // GET /meeting-minutes
    @GetMapping
    @PreAuthorize("hasAuthority('MEETING_MINUTE_VIEW')")
    public ResponseEntity<ApiResponse<PageResponse<
            MeetingMinuteResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer meeting_id) {
        return ResponseEntity.ok(ApiResponse.success(
                meetingMinuteService.getAll(page, size, meeting_id),
                "ទទួលបន្ជីកំណត់ហេតុ"));
    }

    // GET /meeting-minutes/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MEETING_MINUTE_VIEW')")
    public ResponseEntity<ApiResponse<MeetingMinuteResponse>> getById(
            @PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(
                meetingMinuteService.getById(id), "ទទួលបានកំណត់ហេតុ"));
    }

    // POST /meeting-minutes
    @PostMapping
    @PreAuthorize("hasAuthority('MEETING_MINUTE_CREATE')")
    public ResponseEntity<ApiResponse<MeetingMinuteResponse>> create(
            @Valid @RequestBody MeetingMinuteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        meetingMinuteService.create(request),
                        "បង្កើតជោគជ័យ"));
    }

    // PUT /meeting-minutes/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MEETING_MINUTE_CREATE')")
    public ResponseEntity<ApiResponse<MeetingMinuteResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody MeetingMinuteRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                meetingMinuteService.update(id, request),
                "កែប្រែជោគជ័យ"));
    }

    @PostMapping(
            value = "/{id}/attachment",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ATTACHMENT_UPLOAD')")
    public ResponseEntity<ApiResponse<
            MeetingMinuteResponse>>
    uploadAttachment(
            @PathVariable @Positive Integer id,
            @RequestParam MultipartFile file) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        meetingMinuteService
                                .uploadAttachment(id, file),
                        "Upload PDF ជោគជ័យ"));
    }

    // GET /meeting-minutes/{id}/download
    @GetMapping("/{id}/download")
    @PreAuthorize("hasAuthority('MEETING_MINUTE_VIEW')")
    public ResponseEntity<ApiResponse<String>>
    getDownloadUrl(
            @PathVariable @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        meetingMinuteService
                                .getDownloadUrl(id),
                        "URL Download PDF"));
    }
}