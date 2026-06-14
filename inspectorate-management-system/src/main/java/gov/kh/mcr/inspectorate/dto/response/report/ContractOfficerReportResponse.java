package gov.kh.mcr.inspectorate.dto.response.report;

import gov.kh.mcr.inspectorate.enums.Gender;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ContractOfficerReportResponse {

    private Integer   no;
    private String    contractOfficerCode;
    private String    fullNameKh;
    private String    fullNameEn;
    private Gender    gender;
    private String    genderLabel;
    private LocalDate dob;
    private Integer   age;
    @Builder.Default
    private String  departmentName = "";
    private String    jobLevel;
    private String    accountingCode;
    private LocalDate startDate;
    private LocalDate endDate;
    @Builder.Default
    private Long    daysUntilExpiry = 0L;
    @Builder.Default
    private String  expiryLabel    = "";
    private String    note;
    private String    statusCode;
    @Builder.Default
    private String  statusLabel    = "";
}