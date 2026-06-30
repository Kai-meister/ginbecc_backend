package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.request.MeetingRoomRequest;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response.AttachmentResponse;
import gov.kh.mcr.inspectorate.dto.response.MeetingRoomResponse;
import gov.kh.mcr.inspectorate.enums.AttachmentRefType;
import gov.kh.mcr.inspectorate.enums.RoomStatus;
import gov.kh.mcr.inspectorate.service.AttachmentService;
import gov.kh.mcr.inspectorate.service.MeetingRoomService;
import gov.kh.mcr.inspectorate.service.impl.AttachmentServiceImpl;
import gov.kh.mcr.inspectorate.util.AttachmentValidator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/meeting-rooms")
@RequiredArgsConstructor
public class MeetingRoomController {

    private final MeetingRoomService meetingRoomService;

    // GET /meeting-rooms
    @GetMapping
    @PreAuthorize("hasAuthority('ROOM_VIEW')")
    public ResponseEntity<ApiResponse<
        List<MeetingRoomResponse>>>
    getAll(
            @RequestParam(required = false)
            RoomStatus status) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        meetingRoomService.getAll(status),
                        "ទាញយកបញ្ជីបន្ទប់ប្រជុំបានដោយជោគជ័យ"));
    }

    // GET /meeting-rooms/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROOM_VIEW')")
    public ResponseEntity<ApiResponse<
    MeetingRoomResponse>>
    getById(
            @PathVariable
            @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        meetingRoomService.getById(id),
                        "ទាញយកព័ត៌មានបន្ទប់ប្រជុំបានដោយជោគជ័យ"));
    }

    // POST /meeting-rooms
    @PostMapping
    @PreAuthorize("hasAuthority('ROOM_MANAGE')")
    public ResponseEntity<ApiResponse<
    MeetingRoomResponse>>
    create(
            @Valid @RequestBody
            MeetingRoomRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        meetingRoomService.create(request),
                        "បន្ទប់ប្រជុំត្រូវបានបង្កើតដោយជោគជ័យ"));
    }

    // PUT /meeting-rooms/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROOM_MANAGE')")
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
                        "ព័ត៌មានបន្ទប់ប្រជុំត្រូវបានកែប្រែដោយជោគជ័យ"));
    }

    // DELETE /meeting-rooms/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROOM_MANAGE')")
    public ResponseEntity<ApiResponse<Void>>
    delete(
            @PathVariable
            @Positive Integer id) {

        meetingRoomService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success(
                        null, "បន្ទប់ប្រជុំត្រូវបានលុបចេញពីប្រព័ន្ធដោយជោគជ័យ"));
    }

    // POST /meeting-rooms/{id}/image
    @PostMapping(
            value    = "/{id}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROOM_MANAGE')")
    public ResponseEntity<ApiResponse<
    MeetingRoomResponse>>
    uploadImage(
            @PathVariable @Positive Integer id,
            @RequestParam MultipartFile file) {

        AttachmentValidator.validateImage(file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        meetingRoomService
                                .uploadImage(id, file),
                        "រូបភាពបន្ទប់ប្រជុំត្រូវបានផ្ទុកឡើងដោយជោគជ័យ"));
    }

    // DELETE /meeting-rooms/{id}/image
    @DeleteMapping("/{id}/image")
    @PreAuthorize("hasAuthority('ROOM_MANAGE')")
    public ResponseEntity<ApiResponse<
    MeetingRoomResponse>>
    removeImage(
            @PathVariable @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        meetingRoomService.removeImage(id),
                        "រូបភាពបន្ទប់ប្រជុំត្រូវបានដកចេញដោយជោគជ័យ"));
    }

    // GET /image-URL
    @GetMapping("/{id}/image-url")
    public ResponseEntity<ApiResponse<String>>
    getImageUrl(
            @PathVariable @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        meetingRoomService.getImageUrl(id),
                        "ទាញយកតំណភ្ជាប់រូបភាពបានដោយជោគជ័យ"));
    }
}