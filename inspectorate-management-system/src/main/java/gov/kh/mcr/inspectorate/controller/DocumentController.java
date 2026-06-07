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

//    // GET /documents — own
//    @GetMapping
//    @PreAuthorize("hasAnyAuthority('DOCUMENT_VIEW'," + "'DOCUMENT_VIEW_ALL')")
//    public ResponseEntity<ApiResponse<PageResponse<DocumentResponse>>>
//    getAll(
//            @RequestParam(defaultValue = "0")  int page,
//            @RequestParam(defaultValue = "20") int size,
//            @RequestParam(required = false) Integer officer_id,
//            @RequestParam(required = false) Integer type,
//            @RequestParam(required = false) String status,
//            @RequestParam(required = false) Integer expiring_within) {
//
//        if (expiring_within != null) {
//            List<DocumentResponse> list =
//                    documentService.getExpiring(expiring_within);
//            return ResponseEntity.ok(ApiResponse.success(
//                    PageResponse.<DocumentResponse>builder()
//                            .content(list)
//                            .pageNumber(0).pageSize(list.size())
//                            .totalElements(list.size())
//                            .totalPages(1).last(true).first(true).build(),
//                    "ឯកសារជិតផុតកំណត់"));
//        }
//
//        return ResponseEntity.ok(ApiResponse.success(
//                documentService.getAll(
//                        page, size, officer_id, type, status),
//                "ទទួលបន្ជីឯកសារ"));
//    }
//
//    // GET /documents/{id}
//    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyAuthority('DOCUMENT_VIEW'," + "'DOCUMENT_VIEW_ALL')")
//    public ResponseEntity<ApiResponse<DocumentResponse>> getById(
//            @PathVariable Integer id) {
//        return ResponseEntity.ok(ApiResponse.success(
//                documentService.getById(id), "ទទួលបានឯកសារ"));
//    }

    // GET /documents
    @GetMapping
    @PreAuthorize(
            "hasAnyAuthority('DOCUMENT_VIEW',"
                    + "'DOCUMENT_VIEW_ALL')")
    public ResponseEntity<ApiResponse<
    PageResponse<DocumentResponse>>>
    getAll(
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "20")
            int size,
            // Fix — Officer ignores this param
            // Admin/Manager uses it
            @RequestParam(required = false)
            Integer officerId,
            @RequestParam(required = false)
            Integer typeId,
            @RequestParam(required = false)
            String status) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        documentService.getAll(
                                page, size,
                                officerId, typeId, status),
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
                        "ទទួលបានឯកសារ"));
    }
    // POST /documents
    @PostMapping
    @PreAuthorize("hasAuthority('DOCUMENT_CREATE')")
    public ResponseEntity<ApiResponse<DocumentResponse>> create(
            @Valid @RequestBody DocumentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        documentService.create(request),
                        "បង្កើតជោគជ័យ"));
    }

    // PUT /documents/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('DOCUMENT_UPDATE')")
    public ResponseEntity<ApiResponse<DocumentResponse>> update(
            @PathVariable Integer id,
            @Valid @RequestBody DocumentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                documentService.update(id, request),
                "កែប្រែជោគជ័យ"));
    }

    // DELETE /documents/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DOCUMENT_DELETE')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Integer id) {
        documentService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "លុបជោគជ័យ"));
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
                        "Upload ឯកសារជោគជ័យ"));
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
                        "URL Download ឯកសារ"));
    }
}
