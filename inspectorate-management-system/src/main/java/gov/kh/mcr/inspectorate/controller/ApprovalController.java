package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.request.*;
import gov.kh.mcr.inspectorate.dto.response.*;
import gov.kh.mcr.inspectorate.service
        .ApprovalService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost
        .PreAuthorize;
import org.springframework.validation.annotation
        .Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1/approvals")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService
            approvalService;
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
                                .requestApproval(
                                        request),
                        "សសំណើអនុម័តត្រូវបានដាក់ជូនដោយជោគជ័យ"));
    }

    @GetMapping
    @PreAuthorize(
            "hasAnyAuthority('APPROVAL_VIEW',"
                    + "'APPROVAL_REVIEW')")
    public ResponseEntity<ApiResponse<
    PageResponse<ApprovalResponse>>>
    getAll(
            @RequestParam(
                    defaultValue = "0")
            int page,
            @RequestParam(
                    defaultValue = "20")
            int size,
            @RequestParam(
                    required = false)
            String status,
            @RequestParam(
                    required = false)
            Integer officerId,
            @RequestParam(
                    required = false)
            Integer documentId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        approvalService.getAll(
                                page, size, status,
                                officerId,
                                documentId),
                        "ទាញយកបញ្ជីសំណើអនុម័តដោយជោគជ័យ"));
    }

    @GetMapping("/my-pending")
    @PreAuthorize(
            "hasAuthority('APPROVAL_REVIEW')")
    public ResponseEntity<ApiResponse<
    PageResponse<ApprovalResponse>>>
    getMyPending(
            @RequestParam(
                    defaultValue = "0")
            int page,
            @RequestParam(
                    defaultValue = "20")
            int size) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        approvalService
                                .getMyPending(
                                        page, size),
                        "ទាញយកបញ្ជីសំណើដែលកំពុងរង់ចាំការពិនិត្យដោយជោគជ័យ"));
    }

    @GetMapping("/my-requests")
    @PreAuthorize(
            "hasAuthority('APPROVAL_REQUEST')")
    public ResponseEntity<ApiResponse<
    PageResponse<ApprovalResponse>>>
    getMyRequests(
            @RequestParam(
                    defaultValue = "0")
            int page,
            @RequestParam(
                    defaultValue = "20")
            int size,
            @RequestParam(
                    required = false)
            String status) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        approvalService
                                .getMyRequests(
                                        page, size,
                                        status),
                        "ទាញយកប្រវត្តិសំណើរបស់អ្នកប្រើប្រាស់ដោយជោគជ័យ"));
    }

    @GetMapping("/my-decided")
    @PreAuthorize(
            "hasAuthority('APPROVAL_REVIEW')")
    public ResponseEntity<ApiResponse<
    PageResponse<ApprovalResponse>>>
    getMyDecided(
            @RequestParam(
                    defaultValue = "0")
            int page,
            @RequestParam(
                    defaultValue = "20")
            int size) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        approvalService
                                .getMyDecided(
                                        page, size),
                        "ទាញយកបញ្ជីការសម្រេចចិត្តបានដោយជោគជ័យ"));
    }

    @GetMapping("/{id}")
    @PreAuthorize(
            "hasAnyAuthority('APPROVAL_VIEW',"
                    + "'APPROVAL_REVIEW',"
                    + "'APPROVAL_REQUEST')")
    public ResponseEntity<ApiResponse<
    ApprovalResponse>>
    getById(
            @PathVariable
            @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        approvalService.getById(
                                id),
                        "ទាញយកព័ត៌មានសំណើអនុម័តដោយជោគជ័យ"));
    }

    @PutMapping("/{id}/decide")
    @PreAuthorize(
            "hasAuthority('APPROVAL_REVIEW')")
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
                        "សំណើលេខ " + id + " ត្រូវបានកំណត់ស្ថានភាពជា '" + request.getStatusCode() + "' ដោយជោគជ័យ"));
    }
}