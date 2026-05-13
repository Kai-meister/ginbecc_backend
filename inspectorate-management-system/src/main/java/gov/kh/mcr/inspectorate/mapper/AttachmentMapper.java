package gov.kh.mcr.inspectorate.mapper;

import gov.kh.mcr.inspectorate.dto.response.AttachmentResponse;
import gov.kh.mcr.inspectorate.entity.Attachment;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AttachmentMapper {

    @Mapping(target = "fileUrl",
            source = "filePath")
    @Mapping(target = "uploadedBy",
            source = "uploadedBy.userNameKh")
    @Mapping(target = "fileSizeDisplay",
            expression = "java(formatFileSize(entity.getFileSize()))")
    AttachmentResponse toResponse(Attachment entity);

    default String formatFileSize(Long bytes) {
        if (bytes == null || bytes == 0) return "0 B";
        if (bytes < 1024)
            return bytes + " B";
        if (bytes < 1024 * 1024)
            return String.format("%.1f KB",
                    bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024)
            return String.format("%.1f MB",
                    bytes / (1024.0 * 1024));
        return String.format("%.1f GB",
                bytes / (1024.0 * 1024 * 1024));
    }
}