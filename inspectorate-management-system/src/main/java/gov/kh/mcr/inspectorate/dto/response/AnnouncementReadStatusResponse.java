package gov.kh.mcr.inspectorate.dto.response;


import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementReadStatusResponse {

    private Integer announcementId;
    private Long totalRecipients;
    private Long readCount;
    private Long unreadCount;
    private List<RecipientStatus> recipients;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecipientStatus {
        private Integer officerId;
        private String  officerName;
        private Boolean isRead;
        private LocalDateTime readAt;
    }
}
