package gov.kh.mcr.inspectorate.controller;
import gov.kh.mcr.inspectorate.dto.response.ApiResponse;
import gov.kh.mcr.inspectorate.dto.response.AttachmentDownloadResponse;
import gov.kh.mcr.inspectorate.dto.response.AttachmentResponse;
import gov.kh.mcr.inspectorate.entity.Attachment;
import gov.kh.mcr.inspectorate.enums.AttachmentRefType;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.mapper.AttachmentMapper;
import gov.kh.mcr.inspectorate.repository.AttachmentRepository;
import gov.kh.mcr.inspectorate.service.AttachmentService;
import gov.kh.mcr.inspectorate.service.MinioService;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentMapper attachmentMapper;
    private final MinioService minioService;
//    // ─────────────────────────────────────────────
//    // POST /upload — Fix: Enum refType
//    // ─────────────────────────────────────────────
//    @PostMapping(
//            value    = "/upload",
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasAuthority('ATTACHMENT_UPLOAD')")
//    public ResponseEntity<ApiResponse<
//            AttachmentResponse>>
//    upload(
//            @RequestParam MultipartFile file,
//            // Fix — Enum binding
//            @RequestParam
//            @NotNull(message = "ref_type ចាំបាច់")
//            AttachmentRefType ref_type,
//            @RequestParam
//            @NotNull(message = "ref_id ចាំបាច់")
//            @Positive(message = "ref_id > 0")
//            Integer ref_id) {
//
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(ApiResponse.success(
//                        attachmentService.upload(
//                                file, ref_type, ref_id),
//                        "Upload ជោគជ័យ ["
//                                + ref_type.getLabelKh() + "]"));
//    }
//
//    // ─────────────────────────────────────────────
//    // GET /reference/{ref_id}?type=OFFICER
//    // Fix — Enum query param
//    // ─────────────────────────────────────────────
    // GET /attachments/reference/{id}
    @GetMapping("/reference/{id}")
    public ResponseEntity<ApiResponse<
    List<AttachmentResponse>>>
    getByReference(
            @PathVariable
            @Positive Integer ref_id,
            @RequestParam(
                    defaultValue = "OFFICER")
            AttachmentRefType type) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        attachmentService.getByReference(
                                ref_id, type),
                        "ទទួលបន្ជីឯកសារ"));
    }
    // ─────────────────────────────────────────────
    // GET /reference/{ref_id}/active
    // ─────────────────────────────────────────────
    @GetMapping("/reference/{ref_id}/active")
    public ResponseEntity<ApiResponse<
    AttachmentResponse>>
    getActive(
            @PathVariable
            @Positive Integer ref_id,
            @RequestParam(
                    defaultValue = "OFFICER")
            AttachmentRefType type) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        attachmentService.getActiveByReference(
                                ref_id, type),
                        "ទទួល Active File"));
    }
    // ─────────────────────────────────────────────
    // GET /{id}/download
    // ─────────────────────────────────────────────
    @GetMapping("/{id}/download")
    public ResponseEntity<ApiResponse<
    AttachmentDownloadResponse>>
    getDownloadUrl(
            @PathVariable
            @Positive Integer id) {

        Attachment a = attachmentRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ឯកសារ", id));

        String url = minioService
                .getPresignedUrl(a.getFilePath(), 60);

        return ResponseEntity.ok(
                ApiResponse.success(
                        AttachmentDownloadResponse.builder()
                                .attachmentId(id)
                                .downloadUrl(url)
                                .originalName(
                                        a.getOriginalName())
                                .fileType(a.getFileType())
                                .fileSize(
                                        attachmentMapper
                                                .formatFileSize(
                                                        a.getFileSize()))
                                .urlExpiresAt(
                                        LocalDateTime.now()
                                                .plusMinutes(60))
                                .message(
                                        "URL ប្រើបាន 60 នាទី")
                                .build(),
                        "URL Download"));
    }

    // PUT /attachments/{id}/set-current
    @PutMapping("/{id}/set-current")
    @PreAuthorize(
            "hasAuthority('ATTACHMENT_UPLOAD')")
    public ResponseEntity<ApiResponse<
    AttachmentResponse>>
    setActive(
            @PathVariable
            @Positive Integer id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        attachmentService.setActive(id),
                        "ប្ដូរ Active ជោគជ័យ"));
    }

    // DELETE /attachments/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize(
            "hasAuthority('ATTACHMENT_DELETE')")
    public ResponseEntity<ApiResponse<Void>>
    delete(
            @PathVariable
            @Positive Integer id) {

        attachmentService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success(
                        null, "លុបជោគជ័យ"));
    }

    // DELETE /reference/{ref_id}/archived
    @DeleteMapping("/reference/{ref_id}/archived")
    @PreAuthorize("hasAuthority('ATTACHMENT_MANAGE')")
    public ResponseEntity<ApiResponse<Integer>>
    cleanupArchived(
            @PathVariable
            @Positive Integer ref_id,
            @RequestParam(
                    defaultValue = "OFFICER")
            AttachmentRefType type) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        attachmentService.cleanupArchived(
                                ref_id, type),
                        "លុប Archived Files"));
    }
}