package gov.kh.mcr.inspectorate.dto.request;

import gov.kh.mcr.inspectorate.enums.AttendeeRole;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkAttendeeRequest {

    @NotEmpty(message = "បញ្ជីឈ្មោះមន្ត្រីមិនអាចទុកជាទទេបានឡើយ")
    private List<Integer> userIds;

    @NotNull(message = "តួនាទីរបស់អ្នកចូលរួមចាំបាច់ត្រូវតែជ្រើសរើស")
    @Builder.Default
    private AttendeeRole role = AttendeeRole.ATTENDEE;
}