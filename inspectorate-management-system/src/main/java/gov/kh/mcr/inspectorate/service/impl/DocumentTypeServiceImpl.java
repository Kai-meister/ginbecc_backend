package gov.kh.mcr.inspectorate.service.impl;
import gov.kh.mcr.inspectorate.dto.request.ActivityLogContext;
import gov.kh.mcr.inspectorate.dto.request.DocumentTypeRequest;
import gov.kh.mcr.inspectorate.dto.response.DocumentTypeResponse;
import gov.kh.mcr.inspectorate.entity.DocumentType;
import gov.kh.mcr.inspectorate.enums.ActiveStatus;
import gov.kh.mcr.inspectorate.exception.BusinessException;
import gov.kh.mcr.inspectorate.exception.DuplicateResourceException;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.mapper.DocumentTypeMapper;
import gov.kh.mcr.inspectorate.repository.DocumentRepository;
import gov.kh.mcr.inspectorate.repository.DocumentTypeRepository;
import gov.kh.mcr.inspectorate.security.SecurityUtils;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import gov.kh.mcr.inspectorate.service.DocumentTypeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentTypeServiceImpl
        implements DocumentTypeService {

    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentTypeMapper documentTypeMapper;
    private final ActivityLogService activityLogService;
    private final DocumentRepository documentRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public List<DocumentTypeResponse> getAll() {
        return documentTypeRepository.findAll()
                .stream()
                .map(documentTypeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentTypeResponse getById(Integer id) {
        return documentTypeMapper.toResponse(findById(id));
    }

    @Override
    public DocumentTypeResponse create(DocumentTypeRequest request) {
        if (documentTypeRepository.existsByDocumentTypeCode(
                request.getDocumentTypeCode())) {
            throw new DuplicateResourceException(
                    "កូដ [" + request.getDocumentTypeCode()
                            + "] មានស្ទួន");
        }

        DocumentType entity = documentTypeMapper.toEntity(request);

        if (entity.getStatus() == null) {
            entity.setStatus(ActiveStatus.ACTIVE);
        }

        DocumentType saved = documentTypeRepository.save(entity);

        activityLogService.log("CREATE", "DocumentType",
                saved.getDocumentTypeId(),
                "បង្កើត: " + saved.getDocumentTypeName());

        return documentTypeMapper.toResponse(saved);
    }

    @Override
    public DocumentTypeResponse update(
            Integer id,
            DocumentTypeRequest request) {

        DocumentType docType = findById(id);

        // Fix — Block INACTIVE if has documents
        if (request.getStatus()
                == ActiveStatus.INACTIVE
                && docType.getStatus()
                == ActiveStatus.ACTIVE) {

            long count =
                    documentRepository
                            .countByDocumentType_DocumentTypeId(
                                    id);

            if (count > 0) {
                throw new BusinessException(
                        "ប្រភេទឯកសារ \""
                                + docType.getDocumentTypeName()
                                + "\" មានឯកសារ "
                                + count + " — "
                                + "មិនអាច Deactivate");
            }
        }

        documentTypeMapper.updateEntity(
                request, docType);

        activityLogService.log(
                "UPDATE", "DocumentType",
                id,
                "កែប្រែ: "
                        + docType.getDocumentTypeName(),
                buildContext());

        return documentTypeMapper.toResponse(
                documentTypeRepository.save(docType));
    }

    @Override
    public void delete(Integer id) {

        DocumentType type = findById(id);

        long count = documentRepository
                .countByDocumentType_DocumentTypeId(id);

        if (count > 0) {
            throw new BusinessException(
                    "មិនអាចលុបបាន — \""
                            + type.getDocumentTypeName()
                            + "\" មាន " + count
                            + " ឯកសារ"
                            + " — សូម Deactivate ជំនួស");
        }

        documentTypeRepository.deleteById(id);

        activityLogService.log(
                "DELETE", "DocumentType",
                id, "លុបប្រភេទឯកសារ",
                buildContext());
    }
    private ActivityLogContext buildContext() {
        HttpServletRequest request =
                getCurrentRequest();
        return securityUtils.buildLogContext(request);
    }

    // ── Get current HTTP Request ──────────────────
    private HttpServletRequest getCurrentRequest() {
        try {
            return ((ServletRequestAttributes)
                    RequestContextHolder
                            .currentRequestAttributes())
                    .getRequest();
        } catch (Exception e) {
            return null;
        }
    }
    private DocumentType findById(Integer id) {
        return documentTypeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ប្រភេទឯកសារ", id));
    }
}