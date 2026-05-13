package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.request.DocumentTypeRequest;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response.DocumentTypeResponse;
import gov.kh.mcr.inspectorate.service.DocumentTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/document-types")
@RequiredArgsConstructor
public class DocumentTypeController {

    private final DocumentTypeService documentTypeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DocumentTypeResponse>>>
    getAll() {
        return ResponseEntity.ok(ApiResponse.success(
                documentTypeService.getAll(),
                "ទទួលបន្ជីប្រភេទឯកសារ"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentTypeResponse>> getById(
            @PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(
                documentTypeService.getById(id),
                "ទទួលបានប្រភេទឯកសារ"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('OFFICER_MANAGE')")
    public ResponseEntity<ApiResponse<DocumentTypeResponse>> create(
            @Valid @RequestBody DocumentTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        documentTypeService.create(request),
                        "បង្កើតជោគជ័យ"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('OFFICER_MANAGE')")
    public ResponseEntity<ApiResponse<DocumentTypeResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody DocumentTypeRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                documentTypeService.update(id, request),
                "កែប្រែជោគជ័យ"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('OFFICER_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Integer id) {
        documentTypeService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "លុបជោគជ័យ"));
    }
}