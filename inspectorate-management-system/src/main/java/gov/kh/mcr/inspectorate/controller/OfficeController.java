package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.request.OfficeRequest;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response.OfficeResponse;
import gov.kh.mcr.inspectorate.enums.ActiveStatus;
import gov.kh.mcr.inspectorate.service.OfficeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/offices")
@RequiredArgsConstructor
public class OfficeController {

    private final OfficeService officeService;

    @GetMapping
    @PreAuthorize("hasAuthority('OFFICE_VIEW')")
    public ResponseEntity<ApiResponse<List<OfficeResponse>>> getAll(
            @RequestParam(required = false) ActiveStatus status,
            @RequestParam(required = false) String keyword) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        officeService.getAll(status, keyword),
                        "ទទួលបានបញ្ជីទិន្នន័យការិយាល័យ"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('OFFICE_VIEW')")
    public ResponseEntity<ApiResponse<OfficeResponse>> getById(
            @PathVariable @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        officeService.getById(id),
                        "ទទួលបានព័ត៌មានលម្អិតការិយាល័យ"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('OFFICE_MANAGE')")
    public ResponseEntity<ApiResponse<OfficeResponse>> create(
            @Valid @RequestBody OfficeRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        officeService.create(request),
                        "បានបង្កើតការិយាល័យដោយជោគជ័យ"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('OFFICE_MANAGE')")
    public ResponseEntity<ApiResponse<OfficeResponse>> update(
            @PathVariable @Positive Integer id,
            @Valid @RequestBody OfficeRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        officeService.update(id, request),
                        "បានកែប្រែការិយាល័យដោយជោគជ័យ"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('OFFICE_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable @Positive Integer id) {

        officeService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "បានលុបការិយាល័យដោយជោគជ័យ"));
    }
}
