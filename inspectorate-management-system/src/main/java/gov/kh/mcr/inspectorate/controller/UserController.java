package gov.kh.mcr.inspectorate.controller;
import gov.kh.mcr.inspectorate.dto.request.StatusRequest;
import gov.kh.mcr.inspectorate.dto.request.UserRequest;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.dto.response.UserResponse;
import gov.kh.mcr.inspectorate.service.UserService;
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
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public ResponseEntity<ApiResponse<
            PageResponse<UserResponse>>>
    getAll(
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "20")
            int size,
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
                        "ទទួលបន្ជីអ្នកប្រើ"));
    }

    // ─────────────────────────────────────────────
    // GET /users/{id}
    // ─────────────────────────────────────────────
    @GetMapping("/{id}")
    @PreAuthorize(
            "hasAnyAuthority('USER_CREATE',"
                    + "'USER_UPDATE')")
    public ResponseEntity<ApiResponse<
    UserResponse>>
    getById(
            @PathVariable
            @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        userService.getById(id),
                        "ទទួលបានអ្នកប្រើ"));
    }

    // ─────────────────────────────────────────────
    // GET /users/{id}/permissions
    // ─────────────────────────────────────────────
    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('ROLE_ASSIGN')")
    public ResponseEntity<ApiResponse<
    List<String>>>
    getPermissions(
            @PathVariable
            @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        userService.getPermissions(id),
                        "ទទួលសិទ្ធិ"));
    }

    // ─────────────────────────────────────────────
    // POST
    // ─────────────────────────────────────────────
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
                        "បង្កើតអ្នកប្រើជោគជ័យ"));
    }

    // ─────────────────────────────────────────────
    // PUT /{id}
    // ─────────────────────────────────────────────
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
                        "កែប្រែជោគជ័យ"));
    }

    // ─────────────────────────────────────────────
    // PATCH /{id}/status
    // ─────────────────────────────────────────────
    @PatchMapping("/{id}/status")
    @PreAuthorize(
            "hasAnyAuthority('USER_SUSPEND',"
                    + "'USER_UPDATE')")
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
                        "ផ្លាស់ប្ដូរស្ថានភាព"));
    }

    // ─────────────────────────────────────────────
    // DELETE /{id}
    // ─────────────────────────────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_DELETE')")
    public ResponseEntity<ApiResponse<Void>>
    delete(
            @PathVariable
            @Positive Integer id) {

        userService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success(
                        null, "លុបជោគជ័យ"));
    }
}