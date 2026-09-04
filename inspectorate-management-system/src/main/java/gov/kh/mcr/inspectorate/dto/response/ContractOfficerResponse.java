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
    @Builder.Default
    private String officeName = "";
    private Integer officeId;
    @Builder.Default
    private String        departmentName  = "";
    private Integer       departmentId;
    @Builder.Default
    private String             statusCode      = "";
    @Builder.Default
    private String             statusLabel     = "";
    private String        phone;
    private String        email;
    private String        jobLevel;
    private String jobDescription;
    private LocalDate     startDate;
    private LocalDate     endDate;
    private String        note;
    private String        accountingCode;
    @Builder.Default
    private Long            daysUntilExpiry = 0L;
    @Builder.Default
    private String          expiryLabel     = "";
    private String           profileImageUrl;
    private Integer          profileAttachmentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
