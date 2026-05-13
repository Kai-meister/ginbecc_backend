package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.request.ApprovalDecisionRequest;
import gov.kh.mcr.inspectorate.dto.request.ApprovalRequest;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response.ApprovalResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.service.ApprovalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/approvals")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    @GetMapping
    @PreAuthorize("hasAuthority('DOC_APPROVE')")
    public ResponseEntity<ApiResponse<PageResponse<ApprovalResponse>>>
    getAll(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.success(
                approvalService.getAll(page, size, status),
                "ទទួលបន្ជីការអនុម័ត"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('DOC_APPROVE')")
    public ResponseEntity<ApiResponse<ApprovalResponse>> getById(
            @PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(
                approvalService.getById(id), "ទទួលបានការអនុម័ត"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ApprovalResponse>> request(
            @Valid @RequestBody ApprovalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        approvalService.requestApproval(request),
                        "ស្នើអនុម័ត"));
    }

    @PutMapping("/{id}/decide")
    @PreAuthorize("hasAuthority('DOC_APPROVE')")
    public ResponseEntity<ApiResponse<ApprovalResponse>> decide(
            @PathVariable Integer id,
            @Valid @RequestBody ApprovalDecisionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                approvalService.decide(id, request),
                "សម្រេចអនុម័ត"));
    }
}
