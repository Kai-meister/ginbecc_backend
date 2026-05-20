package gov.kh.mcr.inspectorate.dto.request;

import gov.kh.mcr.inspectorate.enums.NotificationType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NotificationCreateRequest {

    private Integer officerId;
    private Integer userId;
    private List<Integer> officerIds;

    @NotBlank(message = "ចំណងជើងចាំបាច់")
    @Size(max = 255,
            message = "ចំណងជើងអតិបរមា 255 តួ")
    private String title;

    @NotBlank(message = "ខ្លឹមសារចាំបាច់")
    private String message;

    @NotNull(message = "ប្រភេទចាំបាច់")
    private NotificationType type;

    private Integer referenceId;

    private String referenceType;
}
