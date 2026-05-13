package gov.kh.mcr.inspectorate.dto.response;

import gov.kh.mcr.inspectorate.enums.Gender;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ContractOfficerResponse {

    private Integer contractOfficerId;
    private String contractOfficerCode;
    private String fullNameKh;
    private String fullNameEn;
    private Gender gender;
    private Integer departmentId;
    private String departmentName;
    private String jobLevel;
    private String jobDescription;
    private String statusCode;
    private String statusLabel;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long daysUntilExpiry;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}