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
    private Integer       age;
    private LocalDate     joinDate;
    private Integer       positionId;
    private String        positionName;
    private Integer       departmentId;
    private String        departmentName;
    private String        jobDescription;
    private String        educationLevel;
    private String        specialization;
    private String        salaryGrade;
    private String        currentAddress;
    private String        birthplace;
    private String        livingStatus;
    private String        phone;
    private String        email;
    private String        statusCode;
    private String        statusLabel;
    private String        profileImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}