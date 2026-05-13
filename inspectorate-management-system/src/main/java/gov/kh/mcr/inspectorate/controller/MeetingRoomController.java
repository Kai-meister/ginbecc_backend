package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.request.MeetingRoomRequest;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response.AttachmentResponse;
import gov.kh.mcr.inspectorate.dto.response.MeetingRoomResponse;
import gov.kh.mcr.inspectorate.enums.RoomStatus;
import gov.kh.mcr.inspectorate.service.MeetingRoomService;
import gov.kh.mcr.inspectorate.util.AttachmentValidator;
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
@RequestMapping("/api/v1/meeting-rooms")
@RequiredArgsConstructor
public class MeetingRoomController {

    private final MeetingRoomService meetingRoomService;

    @GetMapping
    public ResponseEntity<ApiResponse<
        List<MeetingRoomResponse>>>
    getAll(
            @RequestParam(required = false)
            RoomStatus status) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        meetingRoomService.getAll(status),
                        "ទទួលបន្ជីបន្ទប់ប្រជុំ"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<
    MeetingRoomResponse>>
    getById(
            @PathVariable
            @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        meetingRoomService.getById(id),
                        "ទទួលបានបន្ទប់ប្រជុំ"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MEETING_MANAGE')")
    public ResponseEntity<ApiResponse<
    MeetingRoomResponse>>
    create(
            @Valid @RequestBody
            MeetingRoomRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        meetingRoomService.create(request),
                        "បង្កើតបន្ទប់ប្រជុំជោគជ័យ"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MEETING_MANAGE')")
    public ResponseEntity<ApiResponse<
    MeetingRoomResponse>>
    update(
            @PathVariable
            @Positive Integer id,
            @Valid @RequestBody
            MeetingRoomRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        meetingRoomService.update(
                                id, request),
                        "កែប្រែជោគជ័យ"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MEETING_MANAGE')")
    public ResponseEntity<ApiResponse<Void>>
    delete(
            @PathVariable
            @Positive Integer id) {

        meetingRoomService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success(
                        null, "លុបជោគជ័យ"));
    }

    @PostMapping(
            value    = "/{id}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('MEETING_MANAGE')")
    public ResponseEntity<ApiResponse<
            AttachmentResponse>>
    uploadImage(
            @PathVariable
            @Positive Integer id,
            @RequestParam
            org.springframework.web.multipart.MultipartFile file) {
        AttachmentValidator.validateImage(file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        meetingRoomService
                                .uploadImage(id, file),
                        "Upload រូបបន្ទប់ជោគជ័យ"));
    }
}