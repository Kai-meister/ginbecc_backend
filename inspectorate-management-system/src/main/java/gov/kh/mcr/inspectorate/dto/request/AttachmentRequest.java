package gov.kh.mcr.inspectorate.dto.request;
import gov.kh.mcr.inspectorate.enums.AttachmentRefType;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AttachmentRequest {

    @NotNull(message = "ប្រភេទយោងចាំបាច់")
    private AttachmentRefType refType;

    @NotNull(message = "ID យោងចាំបាច់")
    @Positive(message = "ref_id > 0")
    private Integer refId;
}