package gov.kh.mcr.inspectorate.service;

import gov.kh.mcr.inspectorate.dto.request.DocumentRequest;
import gov.kh.mcr.inspectorate.dto.response.DocumentResponse;
import gov.kh.mcr.inspectorate.dto.response.PageResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface DocumentService {

    PageResponse<DocumentResponse> getAll(
            int page, int size,
            Integer officerId,
            Integer typeId,
            String status);

    DocumentResponse getById(Integer id);

    List<DocumentResponse> getExpiring(
            int withinDays);

    DocumentResponse create(
            DocumentRequest request);

    DocumentResponse update(
            Integer id, DocumentRequest request);

    void delete(Integer id);

    // Fix — Upload + Auto-link document file
    DocumentResponse uploadAttachment(
            Integer documentId,
            MultipartFile file);

    // Fix — Get download URL
    String getDownloadUrl(Integer documentId);
}