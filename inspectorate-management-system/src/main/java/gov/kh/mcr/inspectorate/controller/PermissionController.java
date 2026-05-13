package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.entity.Permission;
import gov.kh.mcr.inspectorate.service.PermissionService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ASSIGN')")
    public ResponseEntity<ApiResponse<
        List<Permission>>>
    getAll() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        permissionService.getAll(),
                        "ទទួលបន្ជីសិទ្ធិ"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ASSIGN')")
    public ResponseEntity<ApiResponse<Permission>>
    getById(
            @PathVariable
            @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        permissionService.getById(id),
                        "ទទួលបានសិទ្ធិ"));
    }

    @GetMapping("/module/{module}")
    @PreAuthorize("hasAuthority('ROLE_ASSIGN')")
    public ResponseEntity<ApiResponse<
    List<Permission>>>
    getByModule(
            @PathVariable String module) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        permissionService
                                .getByModule(
                                        module.toUpperCase()),
                        "ទទួលបន្ជីសិទ្ធិតាម Module"));
    }
}