package gov.kh.mcr.inspectorate.dto.request;

import gov.kh.mcr.inspectorate.enums.NotificationType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationCreateRequest {

    private Integer userId;
    private List<Integer> userIds;

    @NotBlank(message = "សូមបញ្ចូលចំណងជើងនៃសេចក្តីជូនដំណឹង")
    @Size(max = 255, message = "ចំណងជើងមិនអាចលើសពី ២៥៥ តួអក្សរឡើយ")
    private String title;

    @NotBlank(message = "សូមបញ្ចូលខ្លឹមសារនៃសេចក្តីជូនដំណឹង")
    private String message;

    @NotNull(message = "សូមជ្រើសរើសប្រភេទនៃសេចក្តីជូនដំណឹង")
    private NotificationType type;

    private Integer referenceId;
}