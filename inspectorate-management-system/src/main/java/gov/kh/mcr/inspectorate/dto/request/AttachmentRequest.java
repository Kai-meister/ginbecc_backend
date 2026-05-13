package gov.kh.mcr.inspectorate.dto.request;

import gov.kh.mcr.inspectorate.enums.AttachmentRefType;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AttachmentRequest {

    @NotBlank(message = "ប្រភេទយោងចាំបាច់")
    @Pattern(
            regexp = AttachmentRefType.VALIDATION_PATTERN,
            message = "ref_type មិនត្រឹមត្រូវ — "
                    + "ប្រើ: OFFICER, CONTRACT_OFFICER, "
                    + "USER, DOCUMENT, MEETING_ROOM, "
                    + "MEETING_MINUTE, ANNOUNCEMENT")
    private String refType;

    @NotNull(message = "ID យោងចាំបាច់")
    @Positive(message = "ref_id ត្រូវជាតួលេខវិជ្ជមាន")
    private Integer refId;
}