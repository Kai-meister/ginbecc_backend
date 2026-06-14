package gov.kh.mcr.inspectorate.dto.request;

import gov.kh.mcr.inspectorate.enums
        .NotificationType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NotificationCreateRequest {

    private Integer       officerId;
    private Integer       userId;
    private List<Integer> officerIds;

    @NotBlank(message = "សូមបញ្ចូលកម្មវត្ថុ ឬចំណងជើងនៃសារដំណឹង")
    @Size(max = 255, message = "ចំណងជើងនៃសារដំណឹងមិនអាចលើសពី ២៥៥ តួអក្សរឡើយ")
    private String title;

    @NotBlank(message = "សូមបញ្ចូលខ្លឹមសារ ឬអត្ថបទនៃសារដំណឹង")
    private String message;

    @NotNull(message = "សូមជ្រើសរើសប្រភេទសារដំណឹង")
    private NotificationType type;

    private Integer referenceId;
}