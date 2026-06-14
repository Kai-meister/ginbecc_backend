package gov.kh.mcr.inspectorate.controller;
import gov.kh.mcr.inspectorate.dto.request.ApprovalRequest;
import gov.kh.mcr.inspectorate.dto.request.DecideRequest;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response.ApprovalResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.service.ApprovalService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1/approvals")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    // GET /approvals
    @GetMapping
    @PreAuthorize(
            "hasAnyAuthority('APPROVAL_VIEW'," + "'APPROVAL_REVIEW')")
    public ResponseEntity<ApiResponse<
                PageResponse<ApprovalResponse>>>
    getAll(
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "20")
            int size,
            @RequestParam(required = false)
            String status,
            @RequestParam(required = false)
            Integer officerId,
            @RequestParam(required = false)
            Integer documentId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        approvalService.getAll(
                                page, size,
                                status, officerId,
                                documentId),
                        "ទទួលបានបញ្ជីការអនុម័ត"));
    }

    // GET /approvals/my-pending
    @GetMapping("/my-pending")
    @PreAuthorize("hasAuthority('APPROVAL_REVIEW')")
    public ResponseEntity<ApiResponse<
    PageResponse<ApprovalResponse>>>
    getMyPending(
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "20")
            int size) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        approvalService.getMyPending(
                                page, size),
                        "ទទួលបានបញ្ជីរង់ចាំការអនុម័ត"));
    }
    // GET /approvals/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('APPROVAL_VIEW',"
                    + "'APPROVAL_REVIEW')")
    public ResponseEntity<ApiResponse<
    ApprovalResponse>>
    getById(@PathVariable @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        approvalService.getById(id),
                        "ទទួលបានព័ត៌មានការអនុម័ត"));
    }

    // POST /approvals - Officer submit
    @PostMapping
    @PreAuthorize(
            "hasAuthority('APPROVAL_REQUEST')")
    public ResponseEntity<ApiResponse<
    ApprovalResponse>>
    requestApproval(
            @Valid @RequestBody
            ApprovalRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        approvalService
                                .requestApproval(request),
                        "បានស្នើសុំការអនុម័តដោយជោគជ័យ"));
    }

    // PUT /approvals/{id}/decide - Admin decide
    @PutMapping("/{id}/decide")
    @PreAuthorize("hasAuthority('APPROVAL_REVIEW')")
    public ResponseEntity<ApiResponse<
    ApprovalResponse>>
    decide(
            @PathVariable
            @Positive Integer id,
            @Valid @RequestBody
            DecideRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        approvalService.decide(
                                id, request),
                        "បានសម្រេចចិត្តការអនុម័ត "
                                + request.getStatusCode()));
    }

}