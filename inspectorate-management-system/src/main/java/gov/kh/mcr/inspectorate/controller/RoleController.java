package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.request.RoleRequest;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response.RoleResponse;
import gov.kh.mcr.inspectorate.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    // GET /roles
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(
                roleService.getAll(), "ទទួលបានបញ្ជីតួនាទីទាំងអស់"));
    }
    // GET /roles/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<ApiResponse<RoleResponse>> getById(
            @PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(
                roleService.getById(id), "ទទួលបានព័ត៌មានតួនាទី"));
    }

    // POST /roles
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ASSIGN')")
    public ResponseEntity<ApiResponse<RoleResponse>> create(
            @Valid @RequestBody RoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        roleService.create(request),
                        "បង្កើតតួនាទីបានជោគជ័យ"));
    }

    // PUT /roles/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ASSIGN')")
    public ResponseEntity<ApiResponse<RoleResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                roleService.update(id, request), "កែប្រែតួនាទីបានជោគជ័យ"));
    }
}
