package gov.kh.mcr.inspectorate.dto.request;

import gov.kh.mcr.inspectorate.enums.AttendeeRole;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendeeRequest {

    @NotNull(message = "លេខសម្គាល់មន្ត្រីចាំបាច់ត្រូវតែមាន")
    @Positive(message = "លេខសម្គាល់មន្ត្រីត្រូវតែជាលេខវិជ្ជមាន")
    private Integer userId;

    @NotNull(message = "តួនាទីរបស់អ្នកចូលរួមចាំបាច់ត្រូវតែជ្រើសរើស")
    private AttendeeRole role;
}