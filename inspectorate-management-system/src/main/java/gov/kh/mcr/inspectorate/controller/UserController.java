package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.request.*;
import gov.kh.mcr.inspectorate.dto.response.*;
import gov.kh.mcr.inspectorate.service.UserService;
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
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET /users
    @GetMapping
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public ResponseEntity<ApiResponse<
    PageResponse<UserResponse>>>
    getAll(
            @RequestParam(
                    defaultValue = "0") int page,
            @RequestParam(
                    defaultValue = "20") int size,
            @RequestParam(required = false)
            Integer roleId,
            @RequestParam(required = false)
            String status,
            @RequestParam(required = false)
            String keyword) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        userService.getAll(
                                page, size,
                                roleId, status, keyword),
                        "ទទួលបានបញ្ជីអ្នកប្រើប្រាស់"));
    }

    // GET /users/{id}
    // Admins (USER_VIEW) can view anyone; any user may view their own
    // profile (SELF_PROFILE_VIEW). Self-view includes officerInfo, which
    // the mobile app needs to resolve the user's avatar.
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_VIEW') or "
            + "(@securityUtils.isSelf(#id) and "
            + "hasAuthority('SELF_PROFILE_VIEW'))")
    public ResponseEntity<ApiResponse<
    UserResponse>>
    getById(
            @PathVariable
            @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        userService.getById(id),
                        "ទទួលបានព័ត៌មានអ្នកប្រើប្រាស់"));
    }

    // GET /users/{id}/permissions
    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    public ResponseEntity<ApiResponse<
    List<String>>>
    getPermissions(
            @PathVariable
            @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        userService.getPermissions(id),
                        "ទទួលបានសិទ្ធិរបស់អ្នកប្រើប្រាស់"));
    }

    // POST /users
    @PostMapping
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public ResponseEntity<ApiResponse<
    UserResponse>>
    create(
            @Valid @RequestBody
            UserRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        userService.create(request),
                        "បង្កើតអ្នកប្រើប្រាស់បានជោគជ័យ"));
    }

    // PUT /users/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<ApiResponse<
    UserResponse>>
    update(
            @PathVariable
            @Positive Integer id,
            @Valid @RequestBody
            UserRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        userService.update(id, request),
                        "កែប្រែព័ត៌មានអ្នកប្រើប្រាស់បានជោគជ័យ"));
    }

    // PATCH /users/{id}/status
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<ApiResponse<
    UserResponse>>
    updateStatus(
            @PathVariable
            @Positive Integer id,
            @Valid @RequestBody
            StatusRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        userService.updateStatus(
                                id, request),
                        "ផ្លាស់ប្ដូរស្ថានភាពអ្នកប្រើប្រាស់បានជោគជ័យ"));
    }

    // DELETE /users/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public ResponseEntity<ApiResponse<Void>>
    delete(
            @PathVariable
            @Positive Integer id) {

        userService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success(
                        null, "លុបអ្នកប្រើប្រាស់បានជោគជ័យ"));
    }
}