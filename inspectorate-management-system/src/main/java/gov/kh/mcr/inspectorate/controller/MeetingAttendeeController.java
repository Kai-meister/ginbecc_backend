package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.request.AttendeeRequest;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response.AttendeeResponse;
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
@RequestMapping("/api/v1/meetings/{meetingId}/attendees")
@RequiredArgsConstructor
public class MeetingAttendeeController {

    private final MeetingAttendeeService
            meetingAttendeeService;

    // ─────────────────────────────────────────────
    // GET — All attendees of meeting
    // ─────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<ApiResponse<
    List<AttendeeResponse>>>
    getByMeeting(
            @PathVariable
            @Positive Integer meetingId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        meetingAttendeeService
                                .getByMeetingId(meetingId),
                        "ទទួលបន្ជីអ្នកចូលរួម"));
    }

    // ─────────────────────────────────────────────
    // GET /{attendeeId} — Fix ខ្វះ
    // ─────────────────────────────────────────────
    @GetMapping("/{attendeeId}")
    public ResponseEntity<ApiResponse<
    AttendeeResponse>>
    getById(
            @PathVariable
            @Positive Integer meetingId,
            @PathVariable
            @Positive Integer attendeeId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        meetingAttendeeService
                                .getById(
                                        meetingId, attendeeId),
                        "ទទួលបានអ្នកចូលរួម"));
    }

    @PostMapping
    @PreAuthorize(
            "hasAuthority('MEETING_MANAGE')")
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
                        meetingAttendeeService
                                .addAttendee(
                                        meetingId, request),
                        "បន្ថែមអ្នកចូលរួមជោគជ័យ"));
    }
    @DeleteMapping("/{attendeeId}")
    @PreAuthorize(
            "hasAuthority('MEETING_MANAGE')")
    public ResponseEntity<ApiResponse<Void>>
    remove(
            @PathVariable
            @Positive Integer meetingId,
            @PathVariable
            @Positive Integer attendeeId) {

        meetingAttendeeService
                .removeAttendee(
                        meetingId, attendeeId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        null, "លុបជោគជ័យ"));
    }
}