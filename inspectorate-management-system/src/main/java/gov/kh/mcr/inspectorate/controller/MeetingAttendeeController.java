package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.request.*;
import gov.kh.mcr.inspectorate.dto.response.*;
import gov.kh.mcr.inspectorate.service
        .MeetingAttendeeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost
        .PreAuthorize;
import org.springframework.validation.annotation
        .Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Validated
@RestController
@RequestMapping(
        "/api/v1/meetings/{meetingId}/attendees")
@RequiredArgsConstructor
public class MeetingAttendeeController {

    private final MeetingAttendeeService
            service;

    @GetMapping
    @PreAuthorize(
            "hasAuthority('MEETING_VIEW')")
    public ResponseEntity<ApiResponse<
    List<AttendeeResponse>>>
    getAll(
            @PathVariable
            @Positive Integer meetingId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        service.getByMeetingId(
                                meetingId),
                        "ទាញយកបញ្ជីអ្នកចូលរួមបានដោយជោគជ័យ"));
    }

    @GetMapping("/{attendeeId}")
    @PreAuthorize(
            "hasAuthority('MEETING_VIEW')")
    public ResponseEntity<ApiResponse<
    AttendeeResponse>>
    getById(
            @PathVariable
            @Positive Integer meetingId,
            @PathVariable
            @Positive Integer attendeeId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        service.getById(
                                meetingId, attendeeId),
                        "ទាញយកព័ត៌មានអ្នកចូលរួមដោយជោគជ័យ"));
    }

    @PostMapping
    @PreAuthorize(
            "hasAuthority"
                    + "('MEETING_MANAGE_ATTENDEES')")
    public ResponseEntity<ApiResponse<
    AttendeeResponse>>
    add(
            @PathVariable
            @Positive Integer meetingId,
            @Valid @RequestBody
            AttendeeRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        service.addAttendee(
                                meetingId, request),
                        "អ្នកចូលរួមត្រូវបានបន្ថែមទៅក្នុងកិច្ចប្រជុំដោយជោគជ័យ"));
    }

    @PostMapping("/bulk")
    @PreAuthorize(
            "hasAuthority"
                    + "('MEETING_MANAGE_ATTENDEES')")
    public ResponseEntity<ApiResponse<
    List<AttendeeResponse>>>
    addBulk(
            @PathVariable
            @Positive Integer meetingId,
            @Valid @RequestBody
            BulkAttendeeRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        service.addBulk(
                                meetingId, request),
                        "ការបន្ថែមបញ្ជីឈ្មោះមន្ត្រីចូលរួមកិច្ចប្រជុំត្រូវបានអនុវត្តដោយជោគជ័យ"));
    }

    @PatchMapping("/{attendeeId}/attendance")
    @PreAuthorize(
            "hasAnyAuthority"
                    + "('MEETING_MANAGE_ATTENDEES',"
                    + "'MEETING_MARK_ATTENDANCE')")
    public ResponseEntity<ApiResponse<
    AttendeeResponse>>
    updateAttendance(
            @PathVariable
            @Positive Integer meetingId,
            @PathVariable
            @Positive Integer attendeeId,
            @Valid @RequestBody
            AttendanceUpdateRequest
                    request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        service.updateAttendance(
                                meetingId, attendeeId,
                                request),
                        "ស្ថានភាពវត្តមានរបស់មន្ត្រីត្រូវបានធ្វើបច្ចុប្បន្នភាពដោយជោគជ័យ"));
    }

    @DeleteMapping("/{attendeeId}")
    @PreAuthorize(
            "hasAuthority"
                    + "('MEETING_MANAGE_ATTENDEES')")
    public ResponseEntity<ApiResponse<Void>>
    remove(
            @PathVariable
            @Positive Integer meetingId,
            @PathVariable
            @Positive Integer attendeeId) {

        service.removeAttendee(
                meetingId, attendeeId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        null, "ការលុបបានជោគជ័យ"));
    }
}