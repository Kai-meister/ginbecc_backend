package gov.kh.mcr.inspectorate.dto.request;

import gov.kh.mcr.inspectorate.enums.AttendeeRole;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BulkAttendeeRequest {

    @NotEmpty(message = "Officer list ចាំបាច់")
    private List<Integer> officerIds;

    @NotNull(message = "តួនាទីចាំបាច់")
    @Builder.Default
    private AttendeeRole role =
            AttendeeRole.ATTENDEE;
}