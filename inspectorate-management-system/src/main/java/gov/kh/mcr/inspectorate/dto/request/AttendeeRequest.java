package gov.kh.mcr.inspectorate.dto.request;

import gov.kh.mcr.inspectorate.enums.AttendeeRole;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AttendeeRequest {

    @NotNull(message = "សូមជ្រើសរើសមន្ត្រីអញ្ជើញចូលរួម")
    @Positive
    private Integer officerId;

    @NotNull(message = "សូមបញ្ជាក់តួនាទីរបស់មន្ត្រីចូលរួម")
    private AttendeeRole role;
}