package gov.kh.mcr.inspectorate.dto.response;

import gov.kh.mcr.inspectorate.enums.Priority;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementResponse {

    private Integer announcementId;
    private String title;
    private String content;
    private String createdByName;
    private LocalDateTime publishAt;
    private String statusCode;
    private String statusLabel;
    private Priority priority;
    private String attachmentUrl;
    private Long  totalRecipients;
    private Long  readCount;
    private LocalDateTime createdAt;
}