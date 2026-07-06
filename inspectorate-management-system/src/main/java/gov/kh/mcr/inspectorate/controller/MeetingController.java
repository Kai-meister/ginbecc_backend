package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.request.*;
import gov.kh.mcr.inspectorate.dto.response.*;
import gov.kh.mcr.inspectorate.service.MeetingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    // GET /meetings
    @GetMapping
    @PreAuthorize("hasAuthority('MEETING_VIEW')")
    public ResponseEntity<ApiResponse<
    PageResponse<MeetingResponse>>>
    getAll(
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "20")
            int size,
            @RequestParam(required = false)
            String status,
            @RequestParam(required = false)
            Integer roomId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        meetingService.getAll(
                                page, size,
                                status, roomId),
                        "ទទួលបានបញ្ជីកិច្ចប្រជុំជោគជ័យ"));
    }

    // GET /meetings/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MEETING_VIEW')")
    public ResponseEntity<ApiResponse<
    MeetingResponse>>
    getById(
            @PathVariable
            @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        meetingService.getById(id),
                        "ទទួលបានព័ត៌មានកិច្ចប្រជុំជោគជ័យ"));
    }

    // GET /meetings/calendar
    @GetMapping("/calendar")
    @PreAuthorize("hasAuthority('MEETING_VIEW')")
    public ResponseEntity<ApiResponse<
    List<MeetingResponse>>>
    getCalendar(
            @RequestParam
            @Min(1) @Max(12)
            int month,
            @RequestParam
            int year) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        meetingService.getCalendar(
                                month, year),
                        "ទទួលបានប្រតិទិនកិច្ចប្រជុំជោគជ័យ"));
    }

    // GET /meetings/rooms/{id}/schedule
    @GetMapping("/rooms/{id}/schedule")
    @PreAuthorize("hasAuthority('ROOM_VIEW')")
    public ResponseEntity<ApiResponse<
    List<MeetingResponse>>>
    getRoomSchedule(
            @PathVariable
            @Positive Integer roomId,
            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat
                            .ISO.DATE)
            LocalDate date) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        meetingService.getRoomSchedule(
                                roomId, date),
                        "ទទួលបានកាលវិភាគបន្ទប់ជោគជ័យ"));
    }

    // POST /meetings
    @PostMapping
    @PreAuthorize("hasAuthority('MEETING_CREATE')")
    public ResponseEntity<ApiResponse<
    MeetingResponse>>
    create(
            @Valid @RequestBody
            MeetingRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        meetingService.create(request),
                        "ការបង្កើតបានជោគជ័យ"));
    }

    // PUT /meetings/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MEETING_UPDATE')")
    public ResponseEntity<ApiResponse<
    MeetingResponse>>
    update(
            @PathVariable
            @Positive Integer id,
            @Valid @RequestBody
            MeetingRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        meetingService.update(
                                id, request),
                        "ការកែប្រែបានជោគជ័យ"));
    }

    // PATCH /meetings/{id}/status
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('MEETING_UPDATE')")
    public ResponseEntity<ApiResponse<
    MeetingResponse>>
    updateStatus(
            @PathVariable
            @Positive Integer id,
            @RequestParam
            @NotBlank
            String statusCode) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        meetingService.updateStatus(
                                id, statusCode),
                        "ការផ្លាស់ប្ដូរស្ថានភាពបានជោគជ័យ"));
    }

    // DELETE /meetings/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MEETING_DELETE')")
    public ResponseEntity<ApiResponse<Void>>
    delete(
            @PathVariable
            @Positive Integer id) {

        meetingService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success(
                        null, "ការលុបបានជោគជ័យ"));
    }


}