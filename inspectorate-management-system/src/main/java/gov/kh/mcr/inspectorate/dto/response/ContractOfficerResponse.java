package gov.kh.mcr.inspectorate.dto.response;

import gov.kh.mcr.inspectorate.enums.Gender;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ContractOfficerResponse {

    private Integer       contractOfficerId;
    private String        contractOfficerCode;
    private String        fullNameKh;
    private String        fullNameEn;
    private Gender        gender;
    private LocalDate     dob;
    private Integer       age;
    private String        departmentName;
    private Integer       departmentId;
    private String        statusCode;
    private String        statusLabel;
    private String        phone;
    private String        email;
    private String        jobLevel;
    private LocalDate     startDate;
    private LocalDate     endDate;
    private String        note;
    private String        accountingCode;
    private Long          daysUntilExpiry;
    private String        expiryLabel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}