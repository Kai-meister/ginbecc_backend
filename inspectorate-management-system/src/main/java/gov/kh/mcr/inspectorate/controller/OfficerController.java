package gov.kh.mcr.inspectorate.controller;


import gov.kh.mcr.inspectorate.dto.request.OfficerRequest;
import gov.kh.mcr.inspectorate.dto.request.StatusRequest;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response.AttachmentResponse;
import gov.kh.mcr.inspectorate.dto.response.OfficerResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.enums.AttachmentRefType;
import gov.kh.mcr.inspectorate.service.AttachmentService;
import gov.kh.mcr.inspectorate.service.OfficerService;
import gov.kh.mcr.inspectorate.util.AttachmentValidator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/officers")
@RequiredArgsConstructor
public class OfficerController {

    private final OfficerService officerService;
    // GET /officers
    @GetMapping
    @PreAuthorize("hasAuthority('OFFICER_VIEW')")
    public ResponseEntity<ApiResponse<PageResponse<OfficerResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer dept,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.success(
                officerService.getAll(page, size, dept, status),
                "ទាញយកបញ្ជីឈ្មោះមន្ត្រីបានដោយជោគជ័យ"));
    }

    // GET /officers/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('OFFICER_VIEW')")
    public ResponseEntity<ApiResponse<OfficerResponse>> getById(
            @PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(
                officerService.getById(id), "ទាញយកព័ត៌មានមន្ត្រីបានដោយជោគជ័យ"));
    }

    // POST /officers
    @PostMapping
    @PreAuthorize("hasAuthority('OFFICER_CREATE')")
    public ResponseEntity<ApiResponse<OfficerResponse>> create(
            @Valid @RequestBody OfficerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        officerService.create(request),
                        "បង្កើតព័ត៌មានមន្ត្រីថ្មីបានដោយជោគជ័យ"));
    }

    // PUT /officers/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('OFFICER_UPDATE')")
    public ResponseEntity<ApiResponse<OfficerResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody OfficerRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                officerService.update(id, request), "ព័ត៌មានមន្ត្រីត្រូវបានកែប្រែដោយជោគជ័យ"));
    }

    // PATCH /officers/{id}/status
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('OFFICER_UPDATE')")
    public ResponseEntity<ApiResponse<OfficerResponse>> updateStatus(
            @PathVariable Integer id,
            @Valid @RequestBody StatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                officerService.updateStatus(id, request),
                "ស្ថានភាពមន្ត្រីត្រូវបានផ្លាស់ប្តូរដោយជោគជ័យ"));
    }

    // DELETE /officers/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('OFFICER_DELETE')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Integer id) {
        officerService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "ព័ត៌មានមន្ត្រីត្រូវបានលុបចេញពីប្រព័ន្ធដោយជោគជ័យ"));
    }

    // GET /officers/near-retirement
    @GetMapping("/near-retirement")
    @PreAuthorize("hasAuthority('OFFICER_VIEW')")
    public ResponseEntity<ApiResponse<List<OfficerResponse>>>
    getNearRetirement() {
        return ResponseEntity.ok(ApiResponse.success(
                officerService.getNearRetirement(),
                "ទាញយកបញ្ជីមន្ត្រីជិតដល់អាយុនិវត្តន៍បានដោយជោគជ័យ"));
    }

    // POST /officers/{id}/profile-image
    @PostMapping(
            value = "/{id}/profile-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ATTACHMENT_UPLOAD')")
    public ResponseEntity<ApiResponse<OfficerResponse>>
    uploadProfileImage(
            @PathVariable @Positive Integer id,
            @RequestParam MultipartFile file) {

        AttachmentValidator.validateImage(file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        officerService
                                .uploadProfileImage(id, file),
                        "រូបភាពប្រវត្តិរូបមន្ត្រីត្រូវបានផ្ទុកឡើងដោយជោគជ័យ"));
    }

    @GetMapping("/{id}/profile-image")
    public ResponseEntity<ApiResponse<String>>
    getProfileImageUrl(
            @PathVariable @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        officerService
                                .getProfileImageUrl(id),
                        "ទទួលបានតំណរូបភាពប្រវត្តិរូប"));
    }


}
