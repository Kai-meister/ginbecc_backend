package gov.kh.mcr.inspectorate.dto.response;

import gov.kh.mcr.inspectorate.enums.UserType;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserResponse {

    private Integer userId;
    private OfficerInfo officerInfo;

    private Integer roleId;
    private String  roleName;
    private String  roleDisplay;

    private UserType userType;
    private String   userNameKh;
    private String   userNameEn;
    private String   email;
    private String   phone;

    private String  statusCode;
    private String  statusLabel;

    private Boolean       mustChangePassword;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Nested OfficerInfo
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OfficerInfo {
        private Integer  id;
        private String   code;
        private String   fullNameKh;
        private String   fullNameEn;
        private String   departmentName;
        private String   phone;
        private String   email;
        private String   jobLevel;   // CONTRACT_OFFICER only
        private UserType type;       // OFFICER / CONTRACT_OFFICER
    }
}