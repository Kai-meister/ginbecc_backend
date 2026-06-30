package gov.kh.mcr.inspectorate.dto.response.report;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OfficerReportResponse {

    private Integer   no;
    private String    officerCode;
    private String    fullNameKh;
    private String    fullNameEn;
    @Builder.Default
    private String    genderLabel    = "";
    private LocalDate dob;
    private Integer   age;
    @Builder.Default
    private String    departmentName = "";
    @Builder.Default
    private String    positionName   = "";
    private LocalDate joinDate;
    private String    phone;
    private String    email;
    private String    educationLevel;
    @Builder.Default
    private String    statusCode     = "";
    @Builder.Default
    private String    statusLabel    = "";
}