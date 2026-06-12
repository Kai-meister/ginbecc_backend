package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.request
        .NotificationCreateRequest;
import gov.kh.mcr.inspectorate.dto.response.*;
import gov.kh.mcr.inspectorate.security.SecurityUtils;
import gov.kh.mcr.inspectorate.service
        .NotificationService;
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
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;
    private final SecurityUtils       securityUtils;

    // POST /notifications
    @PostMapping
    @PreAuthorize(
            "hasAuthority('NOTIFICATION_SEND')")
    public ResponseEntity<ApiResponse<NotificationResponse>>
    create(
            @Valid @RequestBody
            NotificationCreateRequest req) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        service.create(req),
                        "ការបង្កើតសារជូនដំណឹងបានជោគជ័យ"));
    }

    // POST /notifications/bulk
    @PostMapping("/bulk")
    @PreAuthorize(
            "hasAuthority('NOTIFICATION_SEND')")
    public ResponseEntity<ApiResponse<
    List<NotificationResponse>>>
    createBulk(
            @Valid @RequestBody
            NotificationCreateRequest req) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        service.createBulk(req),
                        "ការបង្កើតសារជូនដំណឹងជាច្រើនបានជោគជ័យ"));
    }

    // GET /notifications/my
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<
    PageResponse<NotificationResponse>>>
    getMyNotifications(
            @RequestParam(required = false)
            Boolean isRead,
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "20")
            int size) {
        Integer currentUserId =
                securityUtils.getCurrentUserId();

        return ResponseEntity.ok(
                ApiResponse.success(
                        service.getMyNotifications(
                                currentUserId,
                                isRead, page, size),
                        "ទទួលបានសារជូនដំណឹង"));
    }

    // GET /notifications/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<
    NotificationResponse>>
    getById(
            @PathVariable
            @Positive Integer id) {

        Integer currentUserId =
                securityUtils.getCurrentUserId();

        return ResponseEntity.ok(
                ApiResponse.success(
                        service.getById(
                                id, currentUserId),
                        "ទទួលបានសារជូនដំណឹង"));
    }

    // GET /notifications/unread-count
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>>
    getUnreadCount() {

        Integer currentUserId =
                securityUtils.getCurrentUserId();

        return ResponseEntity.ok(
                ApiResponse.success(
                        service.getUnreadCount(
                                currentUserId),
                        "ចំនួនសារជូនដំណឹងមិនទាន់អាន"));
    }

    // PUT /notifications/{id}/read
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>>
    markAsRead(
            @PathVariable
            @Positive Integer id) {

        Integer currentUserId =
                securityUtils.getCurrentUserId();

        service.markAsRead(id, currentUserId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        null, "បានអានសារជូនដំណឹង"));
    }

    // PUT /notifications/read-all
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Integer>>
    markAllAsRead() {

        Integer currentUserId = securityUtils.getCurrentUserId();

        return ResponseEntity.ok(
                ApiResponse.success(
                        service.markAllAsRead(
                                currentUserId),
                        "បានអានសារជូនដំណឹងទាំងអស់"));
    }
}