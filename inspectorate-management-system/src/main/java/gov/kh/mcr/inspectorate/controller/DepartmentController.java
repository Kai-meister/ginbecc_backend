package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.request.DepartmentRequest;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response.DepartmentResponse;
import gov.kh.mcr.inspectorate.enums.ActiveStatus;
import gov.kh.mcr.inspectorate.service.DepartmentService;
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
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<ApiResponse<
            List<DepartmentResponse>>>
    getAll(
            @RequestParam(required = false)
            ActiveStatus status,
            @RequestParam(required = false)
            String keyword) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        departmentService.getAll(status, keyword),
                        "ទទួលបន្ជីនាយកដ្ឋាន"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<
            DepartmentResponse>>
    getById(
            @PathVariable
            @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        departmentService.getById(id),
                        "ទទួលបាននាយកដ្ឋាន"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('OFFICER_MANAGE')")
    public ResponseEntity<ApiResponse<
            DepartmentResponse>>
    create(
            @Valid @RequestBody
            DepartmentRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        departmentService.create(request),
                        "បង្កើតនាយកដ្ឋានជោគជ័យ"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('OFFICER_MANAGE')")
    public ResponseEntity<ApiResponse<
    DepartmentResponse>>
    update(
            @PathVariable
            @Positive Integer id,
            @Valid @RequestBody
            DepartmentRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        departmentService.update(
                                id, request),
                        "កែប្រែជោគជ័យ"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('OFFICER_MANAGE')")
    public ResponseEntity<ApiResponse<Void>>
    delete(
            @PathVariable
            @Positive Integer id) {

        departmentService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success(
                        null, "លុបជោគជ័យ"));
    }
}