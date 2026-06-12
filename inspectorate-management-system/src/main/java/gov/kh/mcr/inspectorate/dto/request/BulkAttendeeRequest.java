package gov.kh.mcr.inspectorate.dto.request;

import gov.kh.mcr.inspectorate.enums.AttendeeRole;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BulkAttendeeRequest {

    @NotEmpty(message = "សូមជ្រើសរើសបញ្ជីឈ្មោះមន្ត្រីយ៉ាងហោចណាស់ម្នាក់")
    private List<Integer> officerIds;

    @NotNull(message = "សូមបញ្ជាក់តួនាទីរបស់មន្ត្រីចូលរួម")
    @Builder.Default
    private AttendeeRole role =
            AttendeeRole.ATTENDEE;
}