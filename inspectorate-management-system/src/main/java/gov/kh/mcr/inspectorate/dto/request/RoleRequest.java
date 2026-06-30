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

    @NotBlank(message = "សូមបញ្ចូលឈ្មោះសម្រាប់បង្ហាញលើប្រព័ន្ធ")
    @Size(max = 150, message = "ឈ្មោះសម្រាប់បង្ហាញមិនអាចលើសពី ១៥០ តួអក្សរឡើយ")
    private String displayName;

    @Size(max = 1000, message = "ការពិពណ៌នាមិនអាចលើសពី ១០០០ តួអក្សរឡើយ")
    private String description;

    @NotEmpty(message = "សូមជ្រើសរើសសិទ្ធិប្រើប្រាស់យ៉ាងហោចណាស់មួយ")
    private List<Integer> permissionIds;
}