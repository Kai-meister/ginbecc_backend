package gov.kh.mcr.inspectorate.controller;

import gov.kh.mcr.inspectorate.dto.request.DocumentRequest;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response.DocumentResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.service.DocumentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    // GET /documents
    @GetMapping
    @PreAuthorize(
            "hasAnyAuthority('DOCUMENT_VIEW',"
                    + "'DOCUMENT_VIEW_ALL')")
    public ResponseEntity<ApiResponse<
    PageResponse<DocumentResponse>>>
    getAll(
            @RequestParam(
                    defaultValue = "0") int page,
            @RequestParam(
                    defaultValue = "20") int size,

            @RequestParam(required = false)
            Integer userId,
            @RequestParam(required = false)
            Integer typeId,
            @RequestParam(required = false)
            String status) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        documentService.getAll(
                                page, size,
                                userId, typeId, status),
                        "ទទួលបន្ជីឯកសារ"));
    }

// GET /documents/{id}
    @GetMapping("/{id}")
    @PreAuthorize(
            "hasAnyAuthority('DOCUMENT_VIEW',"
                    + "'DOCUMENT_VIEW_ALL')")
    public ResponseEntity<ApiResponse<
    DocumentResponse>>
    getById(
            @PathVariable
            @Positive Integer id) {

        // Service validates owner
        return ResponseEntity.ok(
                ApiResponse.success(
                        documentService.getById(id),
                        "ទទួលបានព័ត៌មានឯកសារ"));
    }
    @PostMapping
    @PreAuthorize(
            "hasAuthority('DOCUMENT_CREATE')")
    public ResponseEntity<ApiResponse<
    DocumentResponse>>
    create(
            @Valid @RequestBody
            DocumentRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        documentService.create(
                                request),
                        "ឯកសារត្រូវបានបង្កើតដោយជោគជ័យ"));
    }
    // PUT /documents/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('DOCUMENT_UPDATE')")
    public ResponseEntity<ApiResponse<DocumentResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody DocumentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                documentService.update(id, request),
                "បានកែប្រែឯកសារដោយជោគជ័យ"));
    }

    // DELETE /documents/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DOCUMENT_DELETE')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Integer id) {
        documentService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "ឯកសារត្រូវបានលុបចេញពីប្រព័ន្ធដោយជោគជ័យ"));
    }

    @PostMapping(value = "/{id}/attachment",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ATTACHMENT_UPLOAD')")
    public ResponseEntity<ApiResponse<DocumentResponse>>
    uploadAttachment(
            @PathVariable @Positive Integer id,
            @RequestParam MultipartFile file) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        documentService
                                .uploadAttachment(id, file),
                        "បានលុបឯកសារដោយជោគជ័យ"));
    }

    // GET /documents/{id}/download
    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyAuthority('DOCUMENT_VIEW',"
                    + "'DOCUMENT_VIEW_ALL')")
    public ResponseEntity<ApiResponse<String>>
    getDownloadUrl(
            @PathVariable @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        documentService.getDownloadUrl(id),
                        "ទទួលបានតំណទាញយកឯកសារ"));
    }
}
