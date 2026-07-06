package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.request
        .NotificationCreateRequest;
import gov.kh.mcr.inspectorate.dto.response.*;
import gov.kh.mcr.inspectorate.enums.NotificationType;
import gov.kh.mcr.inspectorate.exception.BusinessException;
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
                        "សារជូនដំណឹងត្រូវបានបង្កើតដោយជោគជ័យ"));
    }

    // POST /notifications/bulk
    @PostMapping("/bulk")
    @PreAuthorize(
            "hasAuthority('NOTIFICATION_SEND')")
    public ResponseEntity<ApiResponse<
    List<NotificationResponse>>> createBulk(
            @Valid @RequestBody
            NotificationCreateRequest req) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        service.createBulk(req),
                        "សារជូនដំណឹងជាច្រើនត្រូវបានបង្កើតដោយជោគជ័យ"));
    }

    // GET /notifications/my
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<
    PageResponse<NotificationResponse>>>
    getMyNotifications(
            @RequestParam(required = false)
            String type,
            @RequestParam(required = false)
            Boolean isRead,
            @RequestParam(
                    defaultValue = "0")
            int page,
            @RequestParam(
                    defaultValue = "20")
            int size) {

        Integer currentUserId =
                securityUtils.getCurrentUserId();
        NotificationType typeEnum =
                resolveType(type);

        PageResponse<NotificationResponse>
                result = typeEnum != null
                ? service.getMyNotificationsByType(
                currentUserId, typeEnum,
                isRead, page, size)
                : service.getMyNotifications(
                currentUserId, isRead,
                page, size);

        return ResponseEntity.ok(
                ApiResponse.success(result,
                        "ទាញយកបញ្ជីសារជូនដំណឹងរបស់អ្នកបានដោយជោគជ័យ"));
    }
    private NotificationType resolveType(
            String type) {

        if (type == null
                || type.isBlank())
            return null;

        try {
            return NotificationType.valueOf(
                    type.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                    "ប្រភេទសារ (type) មិនត្រឹមត្រូវ: [\" + type + \"]\" \n" +
                            "                    + \" — សូមជ្រើសរើសប្រភេទ: ការប្រជុំ, ឯកសារ, សេចក្តីប្រកាស, ប្រព័ន្ធ");
        }
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
                        "ទាញយកព័ត៌មានសារជូនដំណឹងបានដោយជោគជ័យ"));
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
                        "ទាញយកចំនួនសារជូនដំណឹងដែលមិនទាន់បានអានដោយជោគជ័យ"));
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
                        null, "ស្ថានភាពសារជូនដំណឹងត្រូវបានកែប្រែថាបានអានដោយជោគជ័យ"));
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
                        "សារជូនដំណឹងទាំងអស់ត្រូវបានសម្គាល់ថាបានអានដោយជោគជ័យ"));
    }
}