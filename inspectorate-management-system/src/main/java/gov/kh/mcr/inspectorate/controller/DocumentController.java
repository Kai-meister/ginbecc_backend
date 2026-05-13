package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.request.DocumentRequest;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response.DocumentResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<DocumentResponse>>>
    getAll(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer officer_id,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer expiring_within) {

        if (expiring_within != null) {
            List<DocumentResponse> list =
                    documentService.getExpiring(expiring_within);
            return ResponseEntity.ok(ApiResponse.success(
                    PageResponse.<DocumentResponse>builder()
                            .content(list)
                            .pageNumber(0).pageSize(list.size())
                            .totalElements(list.size())
                            .totalPages(1).last(true).first(true).build(),
                    "ឯកសារជិតផុតកំណត់"));
        }

        return ResponseEntity.ok(ApiResponse.success(
                documentService.getAll(
                        page, size, officer_id, type, status),
                "ទទួលបន្ជីឯកសារ"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DocumentResponse>> getById(
            @PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(
                documentService.getById(id), "ទទួលបានឯកសារ"));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('OFFICER_MANAGE','DOC_VIEW_OWN')")
    public ResponseEntity<ApiResponse<DocumentResponse>> create(
            @Valid @RequestBody DocumentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        documentService.create(request),
                        "បង្កើតជោគជ័យ"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('OFFICER_MANAGE','DOC_VIEW_OWN')")
    public ResponseEntity<ApiResponse<DocumentResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody DocumentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                documentService.update(id, request),
                "កែប្រែជោគជ័យ"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('OFFICER_MANAGE')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Integer id) {
        documentService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "លុបជោគជ័យ"));
    }
}
