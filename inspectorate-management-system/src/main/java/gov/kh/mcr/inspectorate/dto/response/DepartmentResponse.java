package gov.kh.mcr.inspectorate.dto.response;

import gov.kh.mcr.inspectorate.enums.ActiveStatus;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentResponse {

    private Integer departmentId;
    private String departmentCode;
    private String departmentName;
    private String description;
    private ActiveStatus status;
    private LocalDateTime createdAt;
}
