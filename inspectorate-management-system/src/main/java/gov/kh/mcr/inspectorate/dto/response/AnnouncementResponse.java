package gov.kh.mcr.inspectorate.dto.response;

import gov.kh.mcr.inspectorate.enums.Priority;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AnnouncementResponse {

    private Integer       announcementId;
    private String        title;
    private String        content;
    private Priority      priority;
    private String        priorityLabel;
    private String        statusCode;
    private String        statusLabel;
    private LocalDateTime publishAt;

    private LocalDate     expireAt;

    @Builder.Default
    private Boolean       isExpired = false;

    private Long          daysUntilExpire;

    private String        createdByName;
    private Integer       createdById;

    @Builder.Default
    private Long          totalRecipients  = 0L;
    @Builder.Default
    private Long          readCount        = 0L;
    @Builder.Default
    private Long          unreadCount      = 0L;

    private Boolean       currentUserRead;
    private LocalDateTime currentUserReadAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}