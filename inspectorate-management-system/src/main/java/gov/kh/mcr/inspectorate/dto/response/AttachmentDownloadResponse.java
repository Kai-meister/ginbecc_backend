package gov.kh.mcr.inspectorate.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AttachmentDownloadResponse {

    private Integer attachmentId;
    private String downloadUrl;
    private String originalName;
    private String fileType;
    private String fileSize;
    private LocalDateTime urlExpiresAt;
    private String message;
}