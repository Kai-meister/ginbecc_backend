package gov.kh.mcr.inspectorate.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DepartmentManagerResponse {

    private Integer       departmentManagerId;

    private Integer       departmentId;
    private String        departmentName;

    private Integer       userId;
    private String        userNameKh;
    private String        userEmail;
    private String        roleName;

    private Boolean        isPrimary;
    private LocalDateTime  createdAt;
}