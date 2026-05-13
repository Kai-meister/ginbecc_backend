package gov.kh.mcr.inspectorate.service;
import gov.kh.mcr.inspectorate.dto.request.DocumentTypeRequest;
import gov.kh.mcr.inspectorate.dto.response.DocumentTypeResponse;

import java.util.List;

public interface DocumentTypeService {

    List<DocumentTypeResponse> getAll();

    DocumentTypeResponse getById(Integer id);

    DocumentTypeResponse create(DocumentTypeRequest request);

    DocumentTypeResponse update(Integer id, DocumentTypeRequest request);

    void delete(Integer id);
}
