package gov.kh.mcr.inspectorate.dto.response;


import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse {

    private Integer      roleId;
    private String       roleName;
    private String       displayName;
    private String       description;
    private List<String> permissions;
}