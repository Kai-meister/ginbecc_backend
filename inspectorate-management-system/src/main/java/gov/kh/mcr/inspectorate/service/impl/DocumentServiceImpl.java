package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.*;
import gov.kh.mcr.inspectorate.dto.response.*;
import gov.kh.mcr.inspectorate.entity.*;
import gov.kh.mcr.inspectorate.enums
        .ActiveStatus;
import gov.kh.mcr.inspectorate.enums
        .DocumentStatusCode;
import gov.kh.mcr.inspectorate.exception.*;
import gov.kh.mcr.inspectorate.mapper
        .DocumentMapper;
import gov.kh.mcr.inspectorate.repository.*;
import gov.kh.mcr.inspectorate.security
        .SecurityUtils;
import gov.kh.mcr.inspectorate.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation
        .Transactional;
import org.springframework.web.context.request
        .RequestContextHolder;
import org.springframework.web.context.request
        .ServletRequestAttributes;
import org.springframework.web.multipart
        .MultipartFile;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DocumentServiceImpl
        implements DocumentService {

    private final DocumentRepository             documentRepo;
    private final DocumentTypeRepository         documentTypeRepo;
    private final OfficerRepository              officerRepo;
    private final LookupDocumentStatusRepository statusRepo;
    private final AttachmentRepository           attachmentRepo;
    private final DocumentMapper                 documentMapper;
    private final SecurityUtils                  securityUtils;
    private final ActivityLogService             activityLogService;
    private final AttachmentService              attachmentService;
    private final MinioService                   minioService;

    // ─────────────────────────────────────────────
    // GET ALL
    // Fix — Officer sees OWN only
    // ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public PageResponse<DocumentResponse> getAll(
            int page, int size,
            Integer officerId,
            Integer typeId,
            String status) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createdAt").descending());

        // Fix — Check if current user = Officer
        // Officer → filter by own officerId
        // Admin/Manager → filter by param
        Integer resolvedOfficerId =
                resolveOfficerId(officerId);

        Page<Document> result;

        if (resolvedOfficerId != null
                && status != null) {
            result = documentRepo
                    .findByOfficer_OfficerIdAndStatusCode_StatusCode(
                            resolvedOfficerId,
                            status, pageable);
        } else if (resolvedOfficerId != null) {
            result = documentRepo
                    .findByOfficer_OfficerId(
                            resolvedOfficerId, pageable);
        } else if (status != null
                && typeId != null) {
            result = documentRepo
                    .findByStatusCode_StatusCodeAndDocumentType_DocumentTypeId(
                            status, typeId, pageable);
        } else if (status != null) {
            result = documentRepo
                    .findByStatusCode_StatusCode(
                            status, pageable);
        } else if (typeId != null) {
            result = documentRepo
                    .findByDocumentType_DocumentTypeId(
                            typeId, pageable);
        } else {
            result = documentRepo
                    .findAll(pageable);
        }

        return PageResponse.of(
                result.map(
                        documentMapper::toResponse));
    }

    // ─────────────────────────────────────────────
    // GET BY ID
    // Fix — Officer can only view OWN document
    // ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public DocumentResponse getById(Integer id) {

        Document document = findById(id);

        // Fix — owner check
        validateViewPermission(document);

        return documentMapper.toResponse(document);
    }

    // ─────────────────────────────────────────────
    // GET EXPIRING
    // Fix — Officer sees OWN expiring only
    // ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getExpiring(
            int withinDays) {

        LocalDate expiryDate =
                LocalDate.now().plusDays(withinDays);

        List<Document> list;

        // Fix — Officer filter by own
        Officer currentOfficer =
                securityUtils.getCurrentOfficerOrNull();

        if (currentOfficer != null
                && !securityUtils.hasPermission(
                "DOCUMENT_VIEW_ALL")) {
            // Officer → own only
            list = documentRepo
                    .findExpiringByOfficer(
                            expiryDate,
                            currentOfficer.getOfficerId());
        } else {
            // Admin/Manager → all
            list = documentRepo
                    .findExpiring(expiryDate);
        }

        return list.stream()
                .map(documentMapper::toResponse)
                .toList();
    }

    // ─────────────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────────────
    @Override
    public DocumentResponse create(
            DocumentRequest request) {

        DocumentType docType =
                findActiveDocumentType(
                        request.getDocumentTypeId());

        // Fix — Officer can only create
        // document for themselves
        Integer officerId =
                resolveCreateOfficerId(
                        request.getOfficerId());

        Document document =
                documentMapper.toEntity(request);
        document.setDocumentType(docType);
        document.setOfficer(
                findOfficer(officerId));
        document.setStatusCode(
                findStatus(
                        DocumentStatusCode.DRAFT
                                .getCode()));

        securityUtils.getCurrentUser()
                .ifPresent(document::setUploadedBy);

        Document saved =
                documentRepo.save(document);

        activityLogService.log(
                "CREATE", "Document",
                saved.getDocumentId(),
                "បង្កើត: "
                        + saved.getDocumentName(),
                buildContext());

        return documentMapper.toResponse(saved);
    }

    // ─────────────────────────────────────────────
    // UPDATE
    // Fix — Officer can only update OWN document
    // ─────────────────────────────────────────────
    @Override
    public DocumentResponse update(
            Integer id,
            DocumentRequest request) {

        Document document = findById(id);

        // Fix — owner check
        validateOwnership(document);

        // Fix — only DRAFT can be updated
        validateIsDraft(document);

        if (!document.getDocumentType()
                .getDocumentTypeId()
                .equals(
                        request
                                .getDocumentTypeId())) {
            document.setDocumentType(
                    findActiveDocumentType(
                            request.getDocumentTypeId()));
        }

        documentMapper.updateEntity(
                request, document);

        activityLogService.log(
                "UPDATE", "Document",
                id,
                "កែប្រែ: "
                        + document.getDocumentName(),
                buildContext());

        return documentMapper.toResponse(
                documentRepo.save(document));
    }

    // ─────────────────────────────────────────────
    // DELETE
    // Fix — Officer can only delete OWN document
    // ─────────────────────────────────────────────
    @Override
    public void delete(Integer id) {

        Document document = findById(id);

        // Fix — owner check
        validateOwnership(document);

        // Fix — only DRAFT can be deleted
        validateIsDraft(document);

        if (document.getAttachment() != null) {
            attachmentService.delete(
                    document.getAttachment()
                            .getAttachmentId());
        }

        documentRepo.deleteById(id);

        activityLogService.log(
                "DELETE", "Document",
                id,
                "លុប: "
                        + document.getDocumentName(),
                buildContext());
    }

    // ─────────────────────────────────────────────
    // UPLOAD ATTACHMENT
    // Fix — owner check
    // ─────────────────────────────────────────────
    @Override
    public DocumentResponse uploadAttachment(
            Integer documentId,
            MultipartFile file) {

        Document document = findById(documentId);

        // Fix — owner check
        validateOwnership(document);

        var resp = attachmentService.upload(
                file,
                gov.kh.mcr.inspectorate.enums
                        .AttachmentRefType.DOCUMENT,
                documentId);

        attachmentRepo
                .findById(resp.getAttachmentId())
                .ifPresent(att -> {
                    document.setAttachment(att);
                    documentRepo.save(document);
                });

        activityLogService.log(
                "UPDATE", "Document",
                documentId,
                "Upload: "
                        + resp.getOriginalName(),
                buildContext());

        return documentMapper.toResponse(
                documentRepo.save(document));
    }

    // ─────────────────────────────────────────────
    // GET DOWNLOAD URL
    // Fix — owner check
    // ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public String getDownloadUrl(
            Integer documentId) {

        Document document = findById(documentId);

        // Fix — view permission check
        validateViewPermission(document);

        if (document.getAttachment() == null) {
            throw new ResourceNotFoundException(
                    "ឯកសារ មិនមាន File",
                    documentId);
        }

        return minioService.getPresignedUrl(
                document.getAttachment()
                        .getFilePath());
    }

    // ══ Private Helpers ═══════════════════════════

    // ── findById ──────────────────────────────────
    private Document findById(Integer id) {
        return documentRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ឯកសារ", id));
    }

    // Fix ── resolveOfficerId ──────────────────────
    // Officer → force own officerId
    // Admin   → use param (can be null = all)
    private Integer resolveOfficerId(
            Integer requestedOfficerId) {

        Officer currentOfficer =
                securityUtils.getCurrentOfficerOrNull();

        if (currentOfficer != null
                && !securityUtils.hasPermission(
                "DOCUMENT_VIEW_ALL")) {
            // Officer — force own
            return currentOfficer.getOfficerId();
        }

        // Admin/Manager — use requested param
        return requestedOfficerId;
    }

    // Fix ── resolveCreateOfficerId ────────────────
    // Officer → can only create for self
    // Admin   → can specify any officerId
    private Integer resolveCreateOfficerId(
            Integer requestedOfficerId) {

        Officer currentOfficer =
                securityUtils.getCurrentOfficerOrNull();

        if (currentOfficer != null
                && !securityUtils.hasPermission(
                "DOCUMENT_VIEW_ALL")) {
            // Officer — force own
            return currentOfficer.getOfficerId();
        }

        // Admin — must specify officerId
        if (requestedOfficerId == null) {
            throw new BusinessException(
                    "Admin ត្រូវ specify officerId");
        }

        return requestedOfficerId;
    }

    // Fix ── validateViewPermission ────────────────
    // Officer can only view OWN document
    private void validateViewPermission(
            Document document) {

        Officer currentOfficer =
                securityUtils.getCurrentOfficerOrNull();

        // Admin/Manager — can view all
        if (currentOfficer == null
                || securityUtils.hasPermission(
                "DOCUMENT_VIEW_ALL")) {
            return;
        }

        // Officer — own only
        if (document.getOfficer() == null
                || !document.getOfficer()
                .getOfficerId()
                .equals(currentOfficer
                        .getOfficerId())) {
            throw new ResourceNotFoundException(
                    "ឯកសារ", document.getDocumentId());
        }
    }

    // Fix ── validateOwnership ─────────────────────
    // Officer can only modify OWN document
    private void validateOwnership(
            Document document) {

        Officer currentOfficer =
                securityUtils.getCurrentOfficerOrNull();

        // Admin/Manager — can modify all
        if (currentOfficer == null
                || securityUtils.hasPermission(
                "DOCUMENT_VIEW_ALL")) {
            return;
        }

        // Officer — own only
        if (document.getOfficer() == null
                || !document.getOfficer()
                .getOfficerId()
                .equals(currentOfficer
                        .getOfficerId())) {
            throw new BusinessException(
                    "មិនអាច modify ឯកសារ"
                            + " Officer ផ្សេង");
        }
    }

    // Fix ── validateIsDraft ───────────────────────
    private void validateIsDraft(
            Document document) {

        String code =
                document.getStatusCode() != null
                        ? document.getStatusCode()
                          .getStatusCode()
                        : "";

        if (!DocumentStatusCode.isDraft(code)) {
            throw new BusinessException(
                    "ឯកសារ ស្ថានភាព: " + code
                            + " — DRAFT ប៉ុណ្ណោះ"
                            + " អាចកែ/លុប");
        }
    }

    private Officer findOfficer(Integer id) {
        return officerRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មន្ត្រី", id));
    }

    private LookupDocumentStatus findStatus(
            String code) {
        return statusRepo.findById(code)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ស្ថានភាព", code));
    }

    private DocumentType findActiveDocumentType(
            Integer id) {

        DocumentType docType =
                documentTypeRepo.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "ប្រភេទឯកសារ", id));

        if (docType.getStatus()
                != ActiveStatus.ACTIVE) {
            throw new BusinessException(
                    "ប្រភេទឯកសារ \""
                            + docType.getDocumentTypeName()
                            + "\" មិនអាចប្រើ");
        }

        return docType;
    }

    private ActivityLogContext buildContext() {
        try {
            var req =
                    ((ServletRequestAttributes)
                            RequestContextHolder
                                    .currentRequestAttributes())
                            .getRequest();
            return securityUtils
                    .buildLogContext(req);
        } catch (Exception e) {
            return ActivityLogContext.builder()
                    .build();
        }
    }
}