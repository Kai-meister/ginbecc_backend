package gov.kh.mcr.inspectorate.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingMinuteResponse {

    private Integer       minuteId;
    private Integer       meetingId;
    private String        meetingTitle;
    private String        recordedByName;
    private String        summary;
    private String        decisions;
    private String        actionItems;
    private String        attachmentUrl;
    private LocalDateTime createdAt;
}
