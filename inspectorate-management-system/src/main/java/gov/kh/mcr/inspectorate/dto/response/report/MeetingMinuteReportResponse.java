package gov.kh.mcr.inspectorate.dto.response.report;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingMinuteReportResponse {

    private Integer       no;
    @Builder.Default
    private String        meetingTitle  = "";
    private Long meetingId;
    private LocalDate      meetingDate;
    @Builder.Default
    private String         recordedBy   = "";
    private String          summary;
    private String          decisions;
    private String          actionItems;
    @Builder.Default
    private Boolean         hasAttachment = false;
    private LocalDateTime   createdAt;
}