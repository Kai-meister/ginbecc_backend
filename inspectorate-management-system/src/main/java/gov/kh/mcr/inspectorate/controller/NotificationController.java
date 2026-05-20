package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.request.NotificationCreateRequest;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response.NotificationResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.service.NotificationService;
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
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    @PostMapping
    @PreAuthorize(
            "hasAnyAuthority('OFFICER_MANAGE',"
                    + "'USER_CREATE')")
    public ResponseEntity<ApiResponse<
                NotificationResponse>>
    create(
            @Valid @RequestBody
            NotificationCreateRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        notificationService.create(request),
                        "បង្កើតការជូនដំណឹងជោគជ័យ"));
    }

    @PostMapping("/bulk")
    @PreAuthorize(
            "hasAnyAuthority('OFFICER_MANAGE',"
                    + "'USER_CREATE')")
    public ResponseEntity<ApiResponse<
    List<NotificationResponse>>>
    createBulk(
            @Valid @RequestBody
            NotificationCreateRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        notificationService
                                .createBulk(request),
                        "បង្កើតការជូនដំណឹង Bulk ជោគជ័យ"));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<
            PageResponse<NotificationResponse>>>
    getMyNotifications(
            @RequestParam Integer userId,
            @RequestParam(required = false)
            Boolean isRead,
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "20")
            int size) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        notificationService
                                .getMyNotifications(
                                        userId, isRead,
                                        page, size),
                        "ទទួលការជូនដំណឹង"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<
    NotificationResponse>>
    getById(
            @PathVariable
            @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        notificationService.getById(id),
                        "ទទួលបានការជូនដំណឹង"));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>>
    getUnreadCount(
            @RequestParam Integer userId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        notificationService
                                .getUnreadCount(userId),
                        "ចំនួនមិនទាន់អាន"));
    }
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>>
    markAsRead(
            @PathVariable
            @Positive Integer id) {

        notificationService.markAsRead(id);
        return ResponseEntity.ok(
                ApiResponse.success(
                        null, "អានហើយ"));
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<Integer>>
    markAllAsRead(
            @RequestParam Integer userId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        notificationService
                                .markAllAsRead(userId),
                        "អានទាំងអស់ជោគជ័យ"));
    }
}