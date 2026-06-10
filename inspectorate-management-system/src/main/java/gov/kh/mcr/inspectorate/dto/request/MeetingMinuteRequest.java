package gov.kh.mcr.inspectorate.dto.request;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MeetingMinuteRequest {

    @NotNull(message = "សូមជ្រើសរើសកិច្ចប្រជុំ")
    private Integer meetingId;

    @NotBlank(message = "សូមបញ្ចូលសេចក្តីសង្ខេបនៃកិច្ចប្រជុំ")
    private String summary;

    private String decisions;
    private String actionItems;
}