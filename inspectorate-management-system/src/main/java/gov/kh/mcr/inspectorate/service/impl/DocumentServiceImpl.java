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

    private final DocumentRepository
            documentRepo;
    private final DocumentTypeRepository
            documentTypeRepo;
    private final UserRepository
            userRepo;
    private final LookupDocumentStatusRepository
            statusRepo;
    private final AttachmentRepository
            attachmentRepo;
    private final DocumentMapper
            documentMapper;
    private final SecurityUtils
            securityUtils;
    private final ActivityLogService
            activityLogService;
    private final AttachmentService
            attachmentService;
    private final MinioService
            minioService;
    private final ApprovalService
            approvalService;


    @Override
    @Transactional(readOnly = true)
    public PageResponse<DocumentResponse>
    getAll(int page, int size,
           Integer userId,
           Integer typeId,
           String status) {

        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createdAt")
                        .descending());

         boolean canSeeAll =
                securityUtils
                        .canBypassDepartmentScope();

        Page<Document> result;

        if (canSeeAll) {

            if (userId != null
                    && status != null) {
                result = documentRepo
                        .findByUser_UserIdAndStatusCode_StatusCode(
                                userId, status,
                                pageable);
            } else if (userId != null) {
                result = documentRepo
                        .findByUser_UserId(
                                userId, pageable);
            } else if (status != null
                    && typeId != null) {
                result = documentRepo
                        .findByStatusCode_StatusCodeAndDocumentType_DocumentTypeId(
                                status, typeId,
                                pageable);
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
        } else {
             Integer ownDeptId =
                    securityUtils
                            .getCurrentDepartmentId();

            boolean hasViewAll =
                    securityUtils.hasPermission(
                            "DOCUMENT_VIEW_ALL");

            // Honour the status filter here too — it was only applied in
            // the canSeeAll branch, so the mobile Documents tabs (?status=)
            // all returned the same list for non-admin users.
            if (hasViewAll
                    && ownDeptId != null) {
                if (status != null) {
                    result = documentRepo
                            .findByUserDepartmentIdAndStatus(
                                    ownDeptId, status,
                                    pageable);
                } else {
                    result = documentRepo
                            .findByUserDepartmentId(
                                    ownDeptId, pageable);
                }
            } else {
                Integer currentUserId =
                        securityUtils
                                .getCurrentUserId();
                if (status != null) {
                    result = documentRepo
                            .findByUser_UserIdAndStatusCode_StatusCode(
                                    currentUserId,
                                    status, pageable);
                } else {
                    result = documentRepo
                            .findByUser_UserId(
                                    currentUserId,
                                    pageable);
                }
            }
        }

        return PageResponse.of(
                result.map(
                        documentMapper::toResponse));
    }



    @Override
    @Transactional(readOnly = true)
    public DocumentResponse getById(
            Integer id) {

        Document document = findById(id);

        validateViewPermission(document);

        return documentMapper.toResponse(
                document);
    }


    @Override
    public DocumentResponse create(
            DocumentRequest request) {

        DocumentType docType =
                findActiveDocumentType(
                        request.getDocumentTypeId());

        Integer currentUserId =
                securityUtils.getCurrentUserId();

        User currentUser =
                findUser(currentUserId);

        Document document =
                documentMapper.toEntity(request);
        document.setDocumentType(docType);
        document.setUser(currentUser);


        document.setStatusCode(
                findStatus(
                        DocumentStatusCode
                                .DRAFT.getCode()));

        document.setUploadedBy(currentUser);

        Document saved =
                documentRepo.save(document);

        activityLogService.log(
                "CREATE", "Document",
                saved.getDocumentId(),
                "បង្កើត: "
                        + saved.getDocumentName(),
                buildContext());


        return documentMapper.toResponse(
                saved);
    }


    @Override
    public DocumentResponse update(
            Integer id,
            DocumentRequest request) {

        Document document = findById(id);
        validateStrictOwnership(document);
        validateIsDraft(document);

        if (!document.getDocumentType()
                .getDocumentTypeId()
                .equals(request
                        .getDocumentTypeId())) {
            document.setDocumentType(
                    findActiveDocumentType(
                            request
                                    .getDocumentTypeId()));
        }

        documentMapper.updateEntity(
                request, document);

        activityLogService.log(
                "UPDATE", "Document", id,
                "កែប្រែ: "
                        + document.getDocumentName(),
                buildContext());

        return documentMapper.toResponse(
                documentRepo.save(document));
    }

    @Override
    public void delete(Integer id) {

        Document document = findById(id);

        validateStrictOwnership(document);
        validateIsDraft(document);

        if (document.getAttachment()
                != null) {
            attachmentService.delete(
                    document.getAttachment()
                            .getAttachmentId());
        }

        documentRepo.deleteById(id);

        activityLogService.log(
                "DELETE", "Document", id,
                "លុប: "
                        + document.getDocumentName(),
                buildContext());
    }

    @Override
    public DocumentResponse uploadAttachment(
            Integer documentId,
            MultipartFile file) {

        Document document = findById(documentId);

        validateStrictOwnership(document);

        if (document.getAttachment() != null) {
            Integer oldAttachmentId =
                    document.getAttachment()
                            .getAttachmentId();

            document.setAttachment(null);
            documentRepo.save(document);

            try {
                attachmentService.delete(
                        oldAttachmentId);
            } catch (Exception e) {
                log.warn(
                        "Old attachment delete"
                                + " failed for document"
                                + " {}: {}",
                        documentId, e.getMessage());
            }
        }

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

    @Override
    @Transactional(readOnly = true)
    public String getDownloadUrl(
            Integer documentId) {

        Document document =
                findById(documentId);

        validateViewPermission(document);

        if (document.getAttachment()
                == null) {
            throw new
                    ResourceNotFoundException(
                    "ឯកសារ មិនមាន File",
                    documentId);
        }

        return minioService.getPresignedUrl(
                document.getAttachment()
                        .getFilePath());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse>
    getExpiring(int withinDays) {

        LocalDate expiryDate =
                LocalDate.now()
                        .plusDays(withinDays);

        List<Document> list;

        boolean canSeeAll =
                securityUtils
                        .canBypassDepartmentScope();

        if (canSeeAll) {
            list = documentRepo
                    .findExpiring(expiryDate);
        } else {
            Integer ownDeptId =
                    securityUtils
                            .getCurrentDepartmentId();

            boolean hasViewAll =
                    securityUtils.hasPermission(
                            "DOCUMENT_VIEW_ALL");

            if (hasViewAll
                    && ownDeptId != null) {
                list = documentRepo
                        .findExpiringByDepartment(
                                expiryDate,
                                ownDeptId);
            } else {
                Integer currentUserId =
                        securityUtils
                                .getCurrentUserId();
                list = documentRepo
                        .findExpiringByUser(
                                expiryDate,
                                currentUserId);
            }
        }

        return list.stream()
                .map(documentMapper::toResponse)
                .toList();
    }



    private Document findById(Integer id) {
        return documentRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ឯកសារ", id));
    }

    private User findUser(Integer id) {
        return userRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User", id));
    }

    private LookupDocumentStatus findStatus(
            String code) {
        return statusRepo.findById(code)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ស្ថានភាព", code));
    }

    private DocumentType
    findActiveDocumentType(
            Integer id) {

        DocumentType docType =
                documentTypeRepo.findById(id)
                        .orElseThrow(() ->
                                new
                                        ResourceNotFoundException(
                                        "ប្រភេទឯកសារ", id));

        if (docType.getStatus()
                != ActiveStatus.ACTIVE) {
            throw new BusinessException(
                    "ប្រភេទឯកសារ \""
                            + docType
                            .getDocumentTypeName()
                            + "\" មិនអាចប្រើ");
        }

        return docType;
    }

    private void validateViewPermission(
            Document document) {

        if (securityUtils
                .canBypassDepartmentScope()) {
            return;
        }

        Integer docDeptId =
                resolveDocumentDeptId(document);

        boolean hasViewAll =
                securityUtils.hasPermission(
                        "DOCUMENT_VIEW_ALL");

        if (hasViewAll) {

            Integer ownDeptId =
                    securityUtils
                            .getCurrentDepartmentId();

            if (ownDeptId == null
                    || docDeptId == null
                    || !ownDeptId.equals(
                    docDeptId)) {
                throw new
                        ResourceNotFoundException(
                        "ឯកសារ",
                        document
                                .getDocumentId());
            }
            return;
        }

        Integer currentUserId =
                securityUtils
                        .getCurrentUserId();

        if (document.getUser() == null
                || !document.getUser()
                .getUserId()
                .equals(
                        currentUserId)) {
            throw new
                    ResourceNotFoundException(
                    "ឯកសារ",
                    document
                            .getDocumentId());
        }
    }


    private Integer resolveDocumentDeptId(
            Document document) {

        User u = document.getUser();
        if (u == null) return null;

        if (u.getOfficer() != null
                && u.getOfficer()
                .getDepartment()
                != null) {
            return u.getOfficer()
                    .getDepartment()
                    .getDepartmentId();
        }

        if (u.getContractOfficer() != null
                && u.getContractOfficer()
                .getDepartment()
                != null) {
            return u.getContractOfficer()
                    .getDepartment()
                    .getDepartmentId();
        }

        return null;
    }

    private void validateIsDraft(
            Document document) {

        String code =
                document.getStatusCode()
                        != null
                        ? document.getStatusCode()
                        .getStatusCode()
                        : "";

        if (!DocumentStatusCode
                .isDraft(code)) {
            throw new BusinessException(
                    "មិនអាចកែប្រែ ឬលុបបានឡើយ ព្រោះឯកសារនេះមិនស្ថិតក្នុងស្ថានភាព «រក្សាទុកបណ្តោះអាសន្ន/ឯកសារព្រាង» ឡើយ "
                            + "(ស្ថានភាពបច្ចុប្បន្ន: " + code + ")");
        }
    }

    private ActivityLogContext
    buildContext() {
        try {
            var req =
                    ((ServletRequestAttributes)
                            RequestContextHolder
                                    .currentRequestAttributes())
                            .getRequest();
            return securityUtils
                    .buildLogContext(req);
        } catch (Exception e) {
            return ActivityLogContext
                    .builder().build();
        }
    }

    @Override
    public DocumentResponse submitForApproval(
            Integer documentId) {

        Document document =
                findById(documentId);

        validateStrictOwnership(document);
        validateIsDraft(document);

        document.setStatusCode(
                findStatus(
                        DocumentStatusCode
                                .PENDING.getCode()));

        Document saved =
                documentRepo.save(document);
        approvalService
                .autoCreateApproval(saved);

        activityLogService.log(
                "UPDATE", "Document",
                documentId,
                "ស្នើអនុម័ត: "
                        + document.getDocumentName(),
                buildContext());

        return documentMapper.toResponse(
                saved);
    }
    private void validateStrictOwnership(
            Document document) {

        if (securityUtils
                .canBypassDepartmentScope()) {
            return;
        }

        Integer currentUserId =
                securityUtils
                        .getCurrentUserId();

        if (document.getUser() == null
                || !document.getUser()
                .getUserId()
                .equals(
                        currentUserId)) {

            throw new
                    PermissionDeniedException(
                    "កែប្រែ/លុបឯកសារនេះ",
                    "សិទ្ធិប្រតិបត្តិការត្រូវបានបដិសេធ។ មុខងារកែប្រែ ឬលុបនេះត្រូវបានអនុញ្ញាតជូនតែ «ម្ចាស់ឯកសារ (Document Owner)» ផ្ទាល់ប៉ុណ្ណោះ។ "
                            + "ករណីលោកអ្នកជាអ្នកគ្រប់គ្រងប្រព័ន្ធ សូមប្រើប្រាស់គណនីដែលមានតួនាទីជា ADMIN ឬ SUPER_ADMIN ដើម្បីបន្ត។"
            );
        }
    }
}
