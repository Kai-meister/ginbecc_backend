package gov.kh.mcr.inspectorate.dto.response;

import gov.kh.mcr.inspectorate.enums.ActiveStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfficeResponse {

    private Long officeId;
    private String officeCode;
    private String officeName;
    private Integer departmentId;
    private String departmentName;
    private ActiveStatus status ;
    private LocalDateTime createdAt;

}
