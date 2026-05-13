package gov.kh.mcr.inspectorate.controller;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response.NotificationResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.service.NotificationService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService
            notificationService;

    // ─────────────────────────────────────────────
    // GET /notifications/my — ទាំងអស់ of User
    // ─────────────────────────────────────────────
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<
            PageResponse<NotificationResponse>>>
    getMyNotifications(
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "20")
            int size,
            @RequestParam(required = false)
            Boolean isRead) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        notificationService
                                .getMyNotifications(
                                        userId, isRead,
                                        page, size),
                        "ទទួលការជូនដំណឹង"));
    }

    // ─────────────────────────────────────────────
    // GET /notifications/{id} — Fix ខ្វះ
    // ─────────────────────────────────────────────
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