package gov.kh.mcr.inspectorate.controller;
import gov.kh.mcr.inspectorate.dto.request.AttendanceRequest;
import gov.kh.mcr.inspectorate.dto.request.AttendeeRequest;
import gov.kh.mcr.inspectorate.dto.request.MeetingRequest;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response.MeetingResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.dto.response.RoomAvailabilityResponse;
import gov.kh.mcr.inspectorate.service.MeetingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<MeetingResponse>>> getAll(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer room_id) {
        return ResponseEntity.ok(ApiResponse.success(
                meetingService.getAll(page, size, status, room_id),
                "ទទួលបន្ជីប្រជុំ"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MeetingResponse>> getById(
            @PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(
                meetingService.getById(id), "ទទួលបានប្រជុំ"));
    }

    @GetMapping("/calendar")
    public ResponseEntity<ApiResponse<List<MeetingResponse>>> getCalendar(
            @RequestParam int month, @RequestParam int year) {
        return ResponseEntity.ok(ApiResponse.success(
                meetingService.getCalendar(month, year), "ប្រតិទិន"));
    }

    @GetMapping("/rooms/{roomId}/availability")
    public ResponseEntity<ApiResponse<RoomAvailabilityResponse>>
    checkAvailability(
            @PathVariable Integer roomId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            LocalTime startTime,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            LocalTime endTime) {
        return ResponseEntity.ok(ApiResponse.success(
                meetingService.checkAvailability(
                        roomId, date, startTime, endTime),
                "ពិនិត្យ Availability"));
    }

    @GetMapping("/rooms/{roomId}/schedule")
    public ResponseEntity<ApiResponse<List<MeetingResponse>>>
    getRoomSchedule(
            @PathVariable Integer roomId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success(
                meetingService.getRoomSchedule(roomId, date),
                "កាលវិភាគបន្ទប់"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MEETING_MANAGE') or hasAuthority('MEETING_BOOK')")
    public ResponseEntity<ApiResponse<MeetingResponse>> create(
            @Valid @RequestBody MeetingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        meetingService.create(request),
                        "បង្កើតប្រជុំជោគជ័យ"));
    }

    @PostMapping("/{id}/attendees/bulk")
    @PreAuthorize("hasAuthority('MEETING_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> addAttendeesBulk(
            @PathVariable Integer id,
            @RequestBody List<@Valid AttendeeRequest> requests) {
        meetingService.addAttendeesBulk(id, requests);
        return ResponseEntity.ok(
                ApiResponse.success(null, "បន្ថែមអ្នកចូលរួម"));
    }

    @PutMapping("/{id}/attendees/{aid}/attendance")
    public ResponseEntity<ApiResponse<Void>> updateAttendance(
            @PathVariable Integer id,
            @PathVariable Integer aid,
            @Valid @RequestBody AttendanceRequest request) {
        meetingService.updateAttendance(id, aid, request);
        return ResponseEntity.ok(
                ApiResponse.success(null, "ធ្វើបច្ចុប្បន្នភាពវត្តមាន"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MEETING_MANAGE') "
            + "or (hasAuthority('MEETING_BOOK') and @meetingServiceImpl.isOrganizer(#id))")
    public ResponseEntity<ApiResponse<MeetingResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody MeetingRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                meetingService.update(id, request), "កែប្រែ"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MEETING_MANAGE') "
            + "or (hasAuthority('MEETING_BOOK') and @meetingServiceImpl.isOrganizer(#id))")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Integer id) {
        meetingService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "លុបជោគជ័យ"));
    }
}
