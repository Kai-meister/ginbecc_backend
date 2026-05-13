package gov.kh.mcr.inspectorate.dto.request;

import gov.kh.mcr.inspectorate.enums.Priority;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AnnouncementRequest {

    private Integer attachmentPathId;
    private Integer meetingId;

    @NotBlank(message = "ចំណងជើងចាំបាច់")
    @Size(max = 255)
    private String title;

    @NotBlank(message = "ខ្លឹមសារចាំបាច់")
    private String content;

    private LocalDateTime publishAt;

    @NotBlank(message = "ស្ថានភាពចាំបាច់")
    private String statusCode;

    @NotNull(message = "អាទិភាពចាំបាច់")
    private Priority priority;

    @Size(max = 500,
            message = "អ្នកទទួលអតិបរមា 500 នាក់")
    private List<Integer> recipientOfficerIds;
}