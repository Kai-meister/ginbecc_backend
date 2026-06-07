package gov.kh.mcr.inspectorate.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserResponse {

    private Integer       userId;

    // Officer info
    private Integer       officerId;
    private String        officerName;
    private String        departmentName;

    // Role
    private Integer       roleId;
    private String        roleName;
    private String        roleDisplay;

    // Info
    private String        userNameKh;
    private String        userNameEn;
    private String        email;
    private String        phone;

    // Status
    private String        statusCode;
    private String        statusLabel;

    private Boolean       mustChangePassword;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}