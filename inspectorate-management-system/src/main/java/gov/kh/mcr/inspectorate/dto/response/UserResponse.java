package gov.kh.mcr.inspectorate.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Integer       userId;
    private String        uuid;
    private String        userNameKh;
    private String        userNameEn;
    private String        email;
    private String        phone;
    private Integer       roleId;
    private String        roleName;
    private String        roleDisplayName;
    private Integer       officerId;
    private String        officerName;
    private String        officerCode;
    private String        statusCode;
    private String        statusLabel;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}