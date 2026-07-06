package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.request
        .MeetingRoomRequest;
import gov.kh.mcr.inspectorate.dto.response
        .ApiResponse;
import gov.kh.mcr.inspectorate.dto.response
        .MeetingRoomResponse;
import gov.kh.mcr.inspectorate.dto.response
        .RoomScheduleResponse;
import gov.kh.mcr.inspectorate.enums
        .MeetingRoomStatus;
import gov.kh.mcr.inspectorate.service
        .MeetingRoomService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation
        .DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost
        .PreAuthorize;
import org.springframework.validation.annotation
        .Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart
        .MultipartFile;
import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/meeting-rooms")
@RequiredArgsConstructor
public class MeetingRoomController {

    private final MeetingRoomService
            roomService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('MEETING_VIEW', 'ROOM_MANAGE')")
    public ResponseEntity<ApiResponse<
    List<MeetingRoomResponse>>>
    getAll(
            @RequestParam(required = false)
            MeetingRoomStatus status) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        roomService.getAll(status),
                        "ទទួលបន្ជីបន្ទប់ប្រជុំ"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MEETING_VIEW', 'ROOM_MANAGE')")
    public ResponseEntity<ApiResponse<
    MeetingRoomResponse>>
    getById(
            @PathVariable
            @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        roomService.getById(id),
                        "ទទួលបន្ទប់ប្រជុំ"));
    }

    @GetMapping("/{id}/schedule")
    @PreAuthorize("hasAnyAuthority('MEETING_VIEW', 'ROOM_MANAGE')")
    public ResponseEntity<ApiResponse<
    RoomScheduleResponse>>
    getSchedule(
            @PathVariable
            @Positive Integer id,
            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE)
            LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE)
            LocalDate to) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        roomService.getSchedule(
                                id, from, to),
                        "ទទួលតារាងពេលវេលា"
                                + " បន្ទប់ប្រជុំ"));
    }


    @GetMapping("/{id}/availability")
    @PreAuthorize("hasAnyAuthority('MEETING_VIEW', 'ROOM_MANAGE')")
    public ResponseEntity<ApiResponse<
    RoomScheduleResponse>>
    getAvailability(
            @PathVariable
            @Positive Integer id,
            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        roomService.getAvailability(
                                id, date),
                        "ទាញយកព័ត៌មានកាលវិភាគ និងភាពទំនេរនៃបន្ទប់ប្រជុំបានជោគជ័យ"));
    }

    @PostMapping
    @PreAuthorize(
            "hasAuthority('ROOM_MANAGE')")
    public ResponseEntity<ApiResponse<
    MeetingRoomResponse>>
    create(
            @Valid @RequestBody
            MeetingRoomRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        roomService.create(request),
                        "បង្កើតបន្ទប់ប្រជុំ"
                                + "ជោគជ័យ"));
    }

    @PutMapping("/{id}")
    @PreAuthorize(
            "hasAuthority('ROOM_MANAGE')")
    public ResponseEntity<ApiResponse<
    MeetingRoomResponse>>
    update(
            @PathVariable
            @Positive Integer id,
            @Valid @RequestBody
            MeetingRoomRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        roomService.update(
                                id, request),
                        "កែប្រែបន្ទប់ប្រជុំ"
                                + "ជោគជ័យ"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize(
            "hasAuthority('ROOM_MANAGE')")
    public ResponseEntity<ApiResponse<
    MeetingRoomResponse>>
    updateStatus(
            @PathVariable
            @Positive Integer id,
            @RequestParam
            MeetingRoomStatus status) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        roomService.updateStatus(
                                id, status),
                        "បានកែប្រែស្ថានភាពបន្ទប់ប្រជុំទៅជា «" + status.getLabelKh() + "» ដោយជោគជ័យ"));
    }

    @PostMapping(
            value = "/{id}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROOM_MANAGE')")
    public ResponseEntity<ApiResponse<MeetingRoomResponse>>
    uploadImage(
            @PathVariable @Positive Integer id,
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        roomService.uploadRoomImage(id, file),
                        "Upload រូបភាព ជោគជ័យ"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(
            "hasAuthority('ROOM_MANAGE')")
    public ResponseEntity<ApiResponse<Void>>
    delete(
            @PathVariable
            @Positive Integer id) {

        roomService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "លុបបន្ទប់ប្រជុំ"
                                + " ជោគជ័យ"));
    }

    //Me-cl
    @DeleteMapping("/{id}/image")
    @PreAuthorize("hasAuthority('ROOM_MANAGE')")
    public ResponseEntity<ApiResponse<MeetingRoomResponse>> removeImage(
            @PathVariable @Positive Integer id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(
                        roomService.removeImage(id),
                        "លុបរូបភាព ជោគជ័យ"));
    }
}