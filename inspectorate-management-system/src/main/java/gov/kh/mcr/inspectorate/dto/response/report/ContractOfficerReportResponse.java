package gov.kh.mcr.inspectorate.dto.response.report;

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
    @Builder.Default
    private String    genderLabel     = "";
    private LocalDate dob;
    private Integer   age;
    @Builder.Default
    private String    departmentName  = "";
    private String    jobLevel;
    private String    accountingCode;
    private LocalDate startDate;
    private LocalDate endDate;
    @Builder.Default
    private Long      daysUntilExpiry = 0L;
    @Builder.Default
    private String    expiryLabel     = "";
    private String    note;
    @Builder.Default
    private String    statusCode      = "";
    @Builder.Default
    private String    statusLabel     = "";
}