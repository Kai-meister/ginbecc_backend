package gov.kh.mcr.inspectorate.dto.response;

import gov.kh.mcr.inspectorate.enums.Gender;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OfficerResponse {

    private Integer       officerId;
    private String        officerCode;
    private String        fullNameKh;
    private String        fullNameEn;
    private Gender gender;
    private LocalDate     dob;
    private LocalDate     joinDate;
    private Integer       positionId;
    @Builder.Default
    private String        positionName   = "";
    private Integer       officeId;
    @Builder.Default
    private String        officeName = "";
    private Integer       departmentId;
    @Builder.Default
    private String        departmentName = "";
    
    private String        jobDescription;
    private String        educationLevel;
    private String        specialization;
    private String        salaryGrade;
    private String        currentAddress;
    private String        birthplace;
    private String        livingStatus;
    private String        phone;
    private String        email;
    @Builder.Default
    private String           statusCode     = "";
    @Builder.Default
    private String           statusLabel    = "";
    private String        profileImageUrl;
    // fix
    private Integer       profileAttachmentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
