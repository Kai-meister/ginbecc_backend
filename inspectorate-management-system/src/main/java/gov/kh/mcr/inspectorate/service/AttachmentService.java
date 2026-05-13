package gov.kh.mcr.inspectorate.service;

import gov.kh.mcr.inspectorate.dto.response.AttachmentResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface AttachmentService {


    AttachmentResponse upload(MultipartFile file, String refType, Integer refId);

    List<AttachmentResponse> getByReference(
            Integer refId, String refType);

    AttachmentResponse getActiveByReference(
            Integer refId, String refType);

    String getDownloadUrl(Integer id);

    AttachmentResponse setActive(Integer id);

    void delete(Integer id);

    int cleanupArchived(Integer refId, String refType);

    String getFileSizeDisplay(Long bytes);
}