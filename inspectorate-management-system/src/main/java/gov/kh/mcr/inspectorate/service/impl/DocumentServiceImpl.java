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

    @Override
    @Transactional(readOnly = true)
    public DocumentResponse getById(Integer id) {

        Document document = findById(id);

        validateViewPermission(document);

        return documentMapper.toResponse(document);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getExpiring(
            int withinDays) {

        LocalDate expiryDate =
                LocalDate.now().plusDays(withinDays);

        List<Document> list;
        Officer currentOfficer =
                securityUtils.getCurrentOfficerOrNull();

        if (currentOfficer != null
                && !securityUtils.hasPermission(
                "DOCUMENT_VIEW_ALL")) {
            list = documentRepo
                    .findExpiringByOfficer(
                            expiryDate,
                            currentOfficer.getOfficerId());
        } else {
            list = documentRepo
                    .findExpiring(expiryDate);
        }

        return list.stream()
                .map(documentMapper::toResponse)
                .toList();
    }

    @Override
    public DocumentResponse create(
            DocumentRequest request) {

        DocumentType docType =
                findActiveDocumentType(
                        request.getDocumentTypeId());

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
                "បង្កើតឯកសារថ្មី "
                        + saved.getDocumentName(),
                buildContext());

        return documentMapper.toResponse(saved);
    }

    @Override
    public DocumentResponse update(
            Integer id,
            DocumentRequest request) {

        Document document = findById(id);

        validateOwnership(document);
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
                "កកែប្រែព័ត៌មានឯកសារ "
                        + document.getDocumentName(),
                buildContext());

        return documentMapper.toResponse(
                documentRepo.save(document));
    }

    @Override
    public void delete(Integer id) {

        Document document = findById(id);

        validateOwnership(document);

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
                "លុបទិន្នន័យឯកសារ "
                        + document.getDocumentName(),
                buildContext());
    }

    @Override
    public DocumentResponse uploadAttachment(
            Integer documentId,
            MultipartFile file) {

        Document document = findById(documentId);

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
                "បង្ហោះឯកសារ "
                        + resp.getOriginalName(),
                buildContext());

        return documentMapper.toResponse(
                documentRepo.save(document));
    }

    @Override
    @Transactional(readOnly = true)
    public String getDownloadUrl(
            Integer documentId) {

        Document document = findById(documentId);
        validateViewPermission(document);

        if (document.getAttachment() == null) {
            throw new ResourceNotFoundException(
                    "មិនមានឯកសារភ្ជាប់នៅក្នុងប្រព័ន្ធឡើយ សម្រាប់លេខសម្គាល់ ",
                    documentId);
        }

        return minioService.getPresignedUrl(
                document.getAttachment()
                        .getFilePath());
    }

    private Document findById(Integer id) {
        return documentRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មិនមានទិន្នន័យឯកសារដែលមានលេខសម្គាល់ ", id));
    }

    private Integer resolveOfficerId(
            Integer requestedOfficerId) {

        Officer currentOfficer =
                securityUtils.getCurrentOfficerOrNull();

        if (currentOfficer != null
                && !securityUtils.hasPermission(
                "DOCUMENT_VIEW_ALL")) {
            return currentOfficer.getOfficerId();
        }
        return requestedOfficerId;
    }

    private Integer resolveCreateOfficerId(
            Integer requestedOfficerId) {

        Officer currentOfficer =
                securityUtils.getCurrentOfficerOrNull();

        if (currentOfficer != null
                && !securityUtils.hasPermission(
                "DOCUMENT_VIEW_ALL")) {
            return currentOfficer.getOfficerId();
        }

        if (requestedOfficerId == null) {
            throw new BusinessException(
                    "អ្នកគ្រប់គ្រងប្រព័ន្ធ (Admin) ត្រូវតែបញ្ជាក់លេខសម្គាល់មន្ត្រី (Officer ID)");
        }

        return requestedOfficerId;
    }

    private void validateViewPermission(
            Document document) {

        Officer currentOfficer =
                securityUtils.getCurrentOfficerOrNull();

        if (currentOfficer == null
                || securityUtils.hasPermission(
                "DOCUMENT_VIEW_ALL")) {
            return;
        }

        if (document.getOfficer() == null
                || !document.getOfficer()
                .getOfficerId()
                .equals(currentOfficer
                        .getOfficerId())) {
            throw new ResourceNotFoundException(
                    "មិនមានទិន្នន័យឯកសារ ឬអ្នកមិនមានសិទ្ធិចូលមើលឯកសារដែលមានលេខសម្គាល់", document.getDocumentId());
        }
    }

    private void validateOwnership(
            Document document) {

        Officer currentOfficer =
                securityUtils.getCurrentOfficerOrNull();

        if (currentOfficer == null
                || securityUtils.hasPermission(
                "DOCUMENT_VIEW_ALL")) {
            return;
        }

        if (document.getOfficer() == null
                || !document.getOfficer()
                .getOfficerId()
                .equals(currentOfficer
                        .getOfficerId())) {
            throw new BusinessException(
                    "មិនអាចកែប្រែទិន្នន័យឯកសាររបស់មន្ត្រីផ្សេងបានឡើយ");
        }
    }

    private void validateIsDraft(
            Document document) {

        String code =
                document.getStatusCode() != null
                        ? document.getStatusCode()
                          .getStatusCode()
                        : "";

        if (!DocumentStatusCode.isDraft(code)) {
            throw new BusinessException(
                    "មិនអាចកែប្រែ ឬលុបបានឡើយ ដោយសារឯកសារនេះស្ថិតក្នុងស្ថានភាព «"
                            + code
                            + "»។ ប្រព័ន្ធអនុញ្ញាតឱ្យកែប្រែ ឬលុបតែឯកសារដែលស្ថិតក្នុងស្ថានភាព «ឯកសារព្រាង» ប៉ុណ្ណោះ។");
        }
    }

    private Officer findOfficer(Integer id) {
        return officerRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មិនមានទិន្នន័យមន្ត្រីដែលមានលេខសម្គាល់ ", id));
    }

    private LookupDocumentStatus findStatus(
            String code) {
        return statusRepo.findById(code)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "មិនមានទិន្នន័យស្ថានភាពឯកសារដែលមានកូដ ", code));
    }

    private DocumentType findActiveDocumentType(
            Integer id) {

        DocumentType docType =
                documentTypeRepo.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "មិនមានទិន្នន័យប្រភេទឯកសារដែលមានលេខសម្គាល់៖ ", id));

        if (docType.getStatus()
                != ActiveStatus.ACTIVE) {
            throw new BusinessException(
                    "មិនអាចប្រើប្រាស់ប្រភេទឯកសារ «"
                            + docType.getDocumentTypeName()
                            + "» នេះបានឡើយ ដោយសារស្ថានភាពមិនស្ថិតក្នុង «សកម្ម»។");
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