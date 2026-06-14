package gov.kh.mcr.inspectorate.dto.request;
import gov.kh.mcr.inspectorate.enums.AttachmentRefType;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AttachmentRequest {

    @NotNull(message = "សូមជ្រើសរើសប្រភេទយោង")
    private AttachmentRefType refType;

    @NotNull(message = "សូមបញ្ជាក់លេខសម្គាល់យោង")
    @Positive(message = "លេខសម្គាល់យោងត្រូវធំជាង 0")
    private Integer refId;
}