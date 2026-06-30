package gov.kh.mcr.inspectorate.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingMinuteRequest {

    @NotNull(message = "សូមជ្រើសរើសកិច្ចប្រជុំដែលត្រូវធ្វើកំណត់ហេតុ")
    @Positive(message = "លេខសម្គាល់កិច្ចប្រជុំត្រូវតែជាលេខវិជ្ជមាន")
    private Integer meetingId;

    @NotBlank(message = "សូមបញ្ចូលសេចក្តីសង្ខេបនៃកិច្ចប្រជុំ")
    @Size(max = 2000, message = "សេចក្តីសង្ខេបមិនអាចលើសពី ២០០០ តួអក្សរឡើយ")
    private String summary;

    @Size(max = 2000, message = "ការសម្រេចចិត្តមិនអាចលើសពី ២០០០ តួអក្សរឡើយ")
    private String decisions;

    @Size(max = 2000, message = "ចំណុចការងារត្រូវអនុវត្តមិនអាចលើសពី ២០០០ តួអក្សរឡើយ")
    private String actionItems;
}