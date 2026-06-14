package gov.kh.mcr.inspectorate.dto.request;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRequest {

    @NotBlank(message = "សូមបញ្ចូលឈ្មោះតួនាទីប្រព័ន្ធ")
    @Size(max = 100, message = "ឈ្មោះតួនាទីប្រព័ន្ធមិនអាចលើសពី ១០០ តួអក្សរឡើយ")
    private String roleName;

    @Size(max = 150, message = "ឈ្មោះសម្រាប់បង្ហាញមិនអាចលើសពី ១៥០ តួអក្សរឡើយ")
    private String displayName;

    private String       description;
    private List<Integer> permissionIds;
}
