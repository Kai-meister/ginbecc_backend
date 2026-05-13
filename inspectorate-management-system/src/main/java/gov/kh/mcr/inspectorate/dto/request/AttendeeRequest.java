package gov.kh.mcr.inspectorate.dto.request;
import gov.kh.mcr.inspectorate.enums.AttendeeRole;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendeeRequest {

    @NotNull(message = "មន្ត្រីចាំបាច់")
    private Integer officerId;

    @NotNull(message = "តួនាទីចាំបាច់")
    private AttendeeRole role;
}
