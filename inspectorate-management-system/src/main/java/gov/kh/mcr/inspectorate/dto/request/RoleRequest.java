package gov.kh.mcr.inspectorate.dto.request;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRequest {

    @NotBlank(message = "ឈ្មោះតួនាទីចាំបាច់")
    @Size(max = 100)
    private String roleName;

    @Size(max = 150)
    private String displayName;

    private String       description;
    private List<Integer> permissionIds;
}
