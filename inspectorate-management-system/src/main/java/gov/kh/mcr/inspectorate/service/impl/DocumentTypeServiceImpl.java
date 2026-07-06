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
    public List<DocumentTypeResponse>
    getAll() {
         return documentTypeRepository
                .findAllByOrderByDocumentTypeNameAsc()
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
                    "មិនអាចបង្កើតបានឡើយ ដោយសារកូដប្រភេទឯកសារ «"
                            + request.getDocumentTypeCode()
                            + "» នេះមានក្នុងប្រព័ន្ធរួចហើយ។");
        }

        DocumentType entity = documentTypeMapper.toEntity(request);

        if (entity.getStatus() == null) {
            entity.setStatus(ActiveStatus.ACTIVE);
        }

        DocumentType saved = documentTypeRepository.save(entity);

        activityLogService.log("CREATE", "DocumentType",
                saved.getDocumentTypeId(),
                "បង្កើតប្រភេទឯកសារថ្មី " + saved.getDocumentTypeName());

        return documentTypeMapper.toResponse(saved);
    }

    @Override
    public DocumentTypeResponse update(
            Integer id,
            DocumentTypeRequest request) {

        DocumentType docType = findById(id);

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
                        "មិនអាចផ្លាស់ប្តូរស្ថានភាពប្រភេទឯកសារ «"
                                + docType.getDocumentTypeName()
                                + "» ទៅជា «មិនមានសកម្មភាព» បានឡើយ ដោយសារមានឯកសារកំពុងប្រើប្រាស់ប្រភេទនេះចំនួន "
                                + count + " ច្បាប់។");
            }
        }

        documentTypeMapper.updateEntity(
                request, docType);

        activityLogService.log(
                "UPDATE", "DocumentType",
                id,
                "កែប្រែព័ត៌មានប្រភេទឯកសារ "
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
                    "មិនអាចលុបប្រភេទឯកសារ «"
                            + type.getDocumentTypeName()
                            + "» នេះបានឡើយ ដោយសារមានឯកសារកំពុងប្រើប្រាស់ចំនួន "
                            + count
                            + " ច្បាប់។ សូមផ្លាស់ប្តូរស្ថានភាពទៅជា «មិនមានសកម្មភាព» ជំនួសវិញ។");
        }

        documentTypeRepository.deleteById(id);

        activityLogService.log(
                "DELETE", "DocumentType",
                id, "លុបទិន្នន័យប្រភេទឯកសារ",
                buildContext());
    }
    private ActivityLogContext buildContext() {
        HttpServletRequest request =
                getCurrentRequest();
        return securityUtils.buildLogContext(request);
    }

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