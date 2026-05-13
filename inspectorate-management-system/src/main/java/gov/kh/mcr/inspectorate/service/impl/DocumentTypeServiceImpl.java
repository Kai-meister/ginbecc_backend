package gov.kh.mcr.inspectorate.service.impl;
import gov.kh.mcr.inspectorate.dto.request.DocumentTypeRequest;
import gov.kh.mcr.inspectorate.dto.response.DocumentTypeResponse;
import gov.kh.mcr.inspectorate.entity.DocumentType;
import gov.kh.mcr.inspectorate.enums.ActiveStatus;
import gov.kh.mcr.inspectorate.exception.DuplicateResourceException;
import gov.kh.mcr.inspectorate.exception.ResourceNotFoundException;
import gov.kh.mcr.inspectorate.mapper.DocumentTypeMapper;
import gov.kh.mcr.inspectorate.repository.DocumentTypeRepository;
import gov.kh.mcr.inspectorate.service.ActivityLogService;
import gov.kh.mcr.inspectorate.service.DocumentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentTypeServiceImpl
        implements DocumentTypeService {

    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentTypeMapper documentTypeMapper;
    private final ActivityLogService activityLogService;

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
            Integer id, DocumentTypeRequest request) {
        DocumentType entity = findById(id);
        documentTypeMapper.updateEntity(request, entity);

        activityLogService.log("UPDATE", "DocumentType",
                id, "កែប្រែ: " + entity.getDocumentTypeName());

        return documentTypeMapper.toResponse(
                documentTypeRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        findById(id);
        documentTypeRepository.deleteById(id);
        activityLogService.log("DELETE", "DocumentType",
                id, "លុបប្រភេទឯកសារ");
    }

    private DocumentType findById(Integer id) {
        return documentTypeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("ប្រភេទឯកសារ", id));
    }
}