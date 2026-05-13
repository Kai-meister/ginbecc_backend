package gov.kh.mcr.inspectorate.controller;
import gov.kh.mcr.inspectorate.dto.request.ContractOfficerRequest;
import gov.kh.mcr.inspectorate.dto.request.StatusRequest;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response.ContractOfficerResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.service.ContractOfficerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1/contract-officers")
@RequiredArgsConstructor
public class ContractOfficerController {

    private final ContractOfficerService service;
    @GetMapping
    @PreAuthorize("hasAuthority('OFFICER_MANAGE')")
    public ResponseEntity<ApiResponse<
            PageResponse<ContractOfficerResponse>>>
    getAll(
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "20")
            int size,
            @RequestParam(required = false)
            String status,
            @RequestParam(required = false)
            Integer dept,
            @RequestParam(required = false)
            Integer expiring_within) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        service.getAll(
                                page, size,
                                status, dept,
                                expiring_within),
                        "ទទួលបន្ជីមន្ត្រីកិច្ចសន្យា"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('OFFICER_MANAGE')")
    public ResponseEntity<ApiResponse<
    ContractOfficerResponse>>
    getById(
            @PathVariable
            @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        service.getById(id),
                        "ទទួលបានមន្ត្រីកិច្ចសន្យា"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('OFFICER_MANAGE')")
    public ResponseEntity<ApiResponse<
    ContractOfficerResponse>>
    create(
            @Valid @RequestBody
            ContractOfficerRequest req) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        service.create(req),
                        "បង្កើតជោគជ័យ"));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('OFFICER_MANAGE')")
    public ResponseEntity<ApiResponse<
    ContractOfficerResponse>>
    update(
            @PathVariable
            @Positive Integer id,
            @Valid @RequestBody
            ContractOfficerRequest req) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        service.update(id, req),
                        "កែប្រែជោគជ័យ"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('OFFICER_MANAGE')")
    public ResponseEntity<ApiResponse<
    ContractOfficerResponse>>
    updateStatus(
            @PathVariable
            @Positive Integer id,
            @Valid @RequestBody
            StatusRequest req) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        service.updateStatus(id, req),
                        "ផ្លាស់ប្ដូរស្ថានភាព"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('OFFICER_MANAGE')")
    public ResponseEntity<ApiResponse<Void>>
    delete(
            @PathVariable
            @Positive Integer id) {

        service.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success(
                        null, "លុបជោគជ័យ"));
    }
}