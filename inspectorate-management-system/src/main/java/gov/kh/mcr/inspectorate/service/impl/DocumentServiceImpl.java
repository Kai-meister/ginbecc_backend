package gov.kh.mcr.inspectorate.service.impl;

import gov.kh.mcr.inspectorate.dto.request.DocumentRequest;
import gov.kh.mcr.inspectorate.dto.response.DocumentResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import gov.kh.mcr.inspectorate.entity.Document;
import gov.kh.mcr.inspectorate.entity.DocumentType;
import gov.kh.mcr.inspectorate.entity.LookupDocumentStatus;
import gov.kh.mcr.inspectorate.entity.Officer;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.mapper.DocumentMapper;
import gov.kh.mcr.inspectorate.repository.*;
import gov.kh.mcr.inspectorate.security.SecurityUtils;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import gov.kh.mcr.inspectorate.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final OfficerRepository officerRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final AttachmentRepository attachmentRepository;
    private final LookupDocumentStatusRepository lookupStatusRepository;
    private final DocumentMapper documentMapper;
    private final SecurityUtils securityUtils;
    private final ActivityLogService activityLogService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DocumentResponse> getAll(
            int page, int size,
            Integer officerId, Integer typeId, String status) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());

        Page<Document> result;

        if (officerId != null && typeId != null) {
            result = documentRepository
                    .findByOfficer_OfficerIdAndDocumentType_DocumentTypeId(
                            officerId, typeId, pageable);
        } else if (officerId != null && status != null) {
            result = documentRepository
                    .findByOfficer_OfficerIdAndStatusCode_StatusCode(
                            officerId, status, pageable);
        } else if (officerId != null) {
            result = documentRepository
                    .findByOfficer_OfficerId(officerId, pageable);
        } else {
            result = documentRepository.findAll(pageable);
        }

        return PageResponse.of(
                result.map(documentMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentResponse getById(Integer id) {
        return documentMapper.toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getExpiring(int withinDays) {
        return documentRepository
                .findExpiring(LocalDate.now().plusDays(withinDays))
                .stream()
                .map(documentMapper::toResponse)
                .toList();
    }

    @Override
    public DocumentResponse create(DocumentRequest request) {
        Officer officer = officerRepository
                .findById(request.getOfficerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "មន្ត្រី", request.getOfficerId()));

        DocumentType docType = documentTypeRepository
                .findById(request.getDocumentTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ប្រភេទឯកសារ", request.getDocumentTypeId()));

        LookupDocumentStatus status =
                lookupStatusRepository
                        .findById(request.getStatusCode())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "ស្ថានភាព", request.getStatusCode()));

        Document document = documentMapper.toEntity(request);
        document.setOfficer(officer);
        document.setDocumentType(docType);
        document.setStatusCode(status);

        securityUtils.getCurrentUser()
                .ifPresent(document::setUploadedBy);

        if (request.getAttachmentId() != null) {
            attachmentRepository
                    .findById(request.getAttachmentId())
                    .ifPresent(document::setAttachment);
        }

        Document saved = documentRepository.save(document);

        activityLogService.log("CREATE", "Document",
                saved.getDocumentId(),
                "បង្កើត: " + saved.getDocumentName());

        return documentMapper.toResponse(saved);
    }

    @Override
    public DocumentResponse update(
            Integer id, DocumentRequest request) {
        Document document = findById(id);

        document.setDocumentName(request.getDocumentName());
        document.setDocumentNumber(request.getDocumentNumber());
        document.setNote(request.getNote());
        document.setExpiryDate(request.getExpiryDate());

        documentTypeRepository
                .findById(request.getDocumentTypeId())
                .ifPresent(document::setDocumentType);

        lookupStatusRepository
                .findById(request.getStatusCode())
                .ifPresent(document::setStatusCode);

        if (request.getAttachmentId() != null) {
            attachmentRepository
                    .findById(request.getAttachmentId())
                    .ifPresent(document::setAttachment);
        }

        activityLogService.log("UPDATE", "Document",
                id, "កែប្រែ: " + document.getDocumentName());

        return documentMapper.toResponse(
                documentRepository.save(document));
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        documentRepository.deleteById(id);
        activityLogService.log("DELETE", "Document",
                id, "លុបឯកសារ");
    }

    private Document findById(Integer id) {
        return documentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("ឯកសារ", id));
    }
}