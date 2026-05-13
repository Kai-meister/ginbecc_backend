package gov.kh.mcr.inspectorate.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AttachmentResponse {

    private Integer attachmentId;
    private String filePath;
    private String fileUrl;
    private String originalName;
    private String referenceType;
    private Integer referenceId;
    private String fileType;
    private Long fileSize;
    private String fileSizeDisplay;
    private Boolean isActive;
    private String uploadedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}