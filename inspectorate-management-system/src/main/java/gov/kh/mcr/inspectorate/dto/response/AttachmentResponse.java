package gov.kh.mcr.inspectorate.dto.response;

import gov.kh.mcr.inspectorate.enums.AttachmentRefType;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AttachmentResponse {

    private Integer           attachmentId;
    private String            filePath;
    private String            fileUrl;
    private String            originalName;
    private AttachmentRefType referenceType;
    private String            referenceTypeLabel;
    private Integer           referenceId;
    private String            fileType;
    private Long              fileSize;
    private String            fileSizeDisplay;
    private Boolean           isActive;
    private String            uploadedBy;
    private LocalDateTime     createdAt;
    private LocalDateTime     updatedAt;
}