package gov.kh.mcr.inspectorate.service;
import gov.kh.mcr.inspectorate.dto.response.AttachmentResponse;
import gov.kh.mcr.inspectorate.enums.AttachmentRefType;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface AttachmentService {

    // Upload — Enum refType
    AttachmentResponse upload(
            MultipartFile file,
            AttachmentRefType refType,
            Integer refId);

    // Get all (active + archived)
    List<AttachmentResponse> getByReference(
            Integer refId,
            AttachmentRefType refType);

    // Get active only
    AttachmentResponse getActiveByReference(
            Integer refId,
            AttachmentRefType refType);

    // Download URL
    String getDownloadUrl(Integer id);

    // Restore archived → active
    AttachmentResponse setActive(Integer id);

    // Delete (MinIO + DB)
    void delete(Integer id);

    // Delete all archived
    int cleanupArchived(
            Integer refId,
            AttachmentRefType refType);

    // File size display
    String getFileSizeDisplay(Long bytes);
}