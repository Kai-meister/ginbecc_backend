package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.request.*;
import gov.kh.mcr.inspectorate.dto.response.*;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.service.MeetingAttendeeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Validated
@RestController
@RequestMapping(
        "/api/v1/meetings/{meetingId}/attendees")
@RequiredArgsConstructor
public class MeetingAttendeeController {

    private final MeetingAttendeeService service;

    // GET /{meetingId}/attendees
    @GetMapping
    @PreAuthorize("hasAuthority('MEETING_VIEW')")
    public ResponseEntity<ApiResponse<
    List<AttendeeResponse>>>
    getAll(
            @PathVariable
            @Positive Integer meetingId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        service.getByMeetingId(meetingId),
                        "ទទួលបានបញ្ជីអ្នកចូលរួមជោគជ័យ"));
    }

    // GET /{meetingId}/attendees/{id}
    @GetMapping("/{attendeeId}")
    @PreAuthorize("hasAuthority('MEETING_VIEW')")
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
                        "ទទួលបានព័ត៌មានអ្នកចូលរួមជោគជ័យ"));
    }
    // POST /{meetingId}/attendees
    @PostMapping
    @PreAuthorize("hasAuthority('MEETING_MANAGE_ATTENDEES')")
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
                        "បន្ថែមអ្នកចូលរួមជោគជ័យ"));

    }

    // POST /{meetingId}/attendees/bulk
    @PostMapping("/bulk")
    @PreAuthorize(
            "hasAuthority('MEETING_MANAGE_ATTENDEES')")
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
                        "បន្ថែមអ្នកចូលរួមជាច្រើនជោគជ័យ"));
    }

    // PATCH /{meetingId}/attendees/{id}/attendance
    @PatchMapping("/{attendeeId}/attendance")
    @PreAuthorize(
            "hasAuthority('MEETING_MARK_ATTENDANCE')")
    public ResponseEntity<ApiResponse<
    AttendeeResponse>>
    updateAttendance(
            @PathVariable
            @Positive Integer meetingId,
            @PathVariable
            @Positive Integer attendeeId,
            @Valid @RequestBody
            AttendanceUpdateRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        service.updateAttendance(
                                meetingId, attendeeId,
                                request),
                        "ធ្វើបច្ចុប្បន្នភាពស្ថានភាពការចូលរួមជោគជ័យ"));
    }

    // DELETE /{meetingId}/attendees/{id}
    @DeleteMapping("/{attendeeId}")
    @PreAuthorize("hasAuthority('MEETING_MANAGE_ATTENDEES')")
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
                        null, "លុបអ្នកចូលរួមជោគជ័យ"));
    }
}