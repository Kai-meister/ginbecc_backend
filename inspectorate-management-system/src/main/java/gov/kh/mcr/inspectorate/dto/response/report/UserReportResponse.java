package gov.kh.mcr.inspectorate.dto.response.report;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserReportResponse {

    private Integer       no;
    private String        userNameKh;
    private String        userNameEn;
    private String        email;
    private String        phone;
    @Builder.Default
    private String        roleName     = "";
    @Builder.Default
    private String        roleDisplay  = "";
    private String        officerName;
    private String        departmentName;
    @Builder.Default
    private String        statusCode   = "";
    @Builder.Default
    private String        statusLabel  = "";
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
}